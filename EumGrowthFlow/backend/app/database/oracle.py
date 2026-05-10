"""
Oracle Database 연결 유틸리티

python-oracledb Thin 모드를 사용하여 Oracle Client 설치 없이 동작한다.
비동기 커넥션 풀을 통해 FastAPI 와 통합하여 사용할 수 있다.

사용 예:
    db = OracleDB.from_settings()
    await db.start()
    async with db.connection() as conn:
        result = await conn.execute("SELECT * FROM users")
    await db.close()
"""

import logging
from contextlib import asynccontextmanager
from dataclasses import dataclass
from typing import AsyncIterator

import oracledb

logger = logging.getLogger("eumgrowthflow.oracle")


@dataclass
class OracleConfig:
    """Oracle DB 접속 정보"""

    host: str = "localhost"
    port: int = 1521
    service_name: str = "XEPDB1"
    user: str = "app_user"
    password: str = ""
    # 읽기 전용 계정 (별도 설정 시 사용)
    ro_user: str = ""
    ro_password: str = ""
    # 커넥션 풀 설정
    pool_min: int = 1
    pool_max: int = 5
    pool_increment: int = 1

    @property
    def dsn(self) -> str:
        return f"{self.host}:{self.port}/{self.service_name}"

    @property
    def has_readonly_user(self) -> bool:
        creds_set = bool(self.ro_user and self.ro_password)
        same_as_rw = (
            self.ro_user == self.user
            and self.ro_password == self.password
        )
        return creds_set and not same_as_rw


class OracleDB:
    """Oracle Database 비동기 연결 관리자

    내부적으로 oracledb.create_pool_async() 로 커넥션 풀을 생성하며,
    읽기 전용 계정이 설정된 경우 별도 풀을 관리한다.

    기본 사용:
        db = OracleDB(config)
        await db.start()
        async with db.connection() as conn:
            rows = await conn.execute("SELECT ...")
        await db.close()

    읽기 전용:
        async with db.connection(readonly=True) as conn:
            rows = await conn.execute("SELECT ...")  # SELECT 만 가능
    """

    def __init__(self, config: OracleConfig) -> None:
        self.config = config
        self._pool: oracledb.AsyncConnectionPool | None = None
        self._ro_pool: oracledb.AsyncConnectionPool | None = None

    @classmethod
    def from_settings(cls) -> "OracleDB":
        """config.settings 에서 Oracle 설정을 읽어 인스턴스 생성"""
        from config import settings

        return cls(
            OracleConfig(
                host=settings.oracle_host,
                port=settings.oracle_port,
                service_name=settings.oracle_service_name,
                user=settings.oracle_user,
                password=settings.oracle_password,
                ro_user=settings.oracle_ro_user,
                ro_password=settings.oracle_ro_password,
                pool_min=settings.oracle_pool_min,
                pool_max=settings.oracle_pool_max,
                pool_increment=settings.oracle_pool_increment,
            )
        )

    async def start(self) -> None:
        """커넥션 풀 초기화 (앱 시작 시 한 번 호출)

        oracledb.create_pool_async() 는 Thin 모드에서 즉시 객체를 반환하며,
        실제 연결은 acquire() 시점에 지연 생성된다.
        """
        logger.info(
            "Oracle 연결 중: %s:%d/%s (user=%s)",
            self.config.host,
            self.config.port,
            self.config.service_name,
            self.config.user,
        )
        self._pool = oracledb.create_pool_async(
            user=self.config.user,
            password=self.config.password,
            dsn=self.config.dsn,
            min=self.config.pool_min,
            max=self.config.pool_max,
            increment=self.config.pool_increment,
        )
        if self.config.has_readonly_user:
            logger.info(
                "Oracle 읽기 전용 연결 중: user=%s", self.config.ro_user
            )
            self._ro_pool = oracledb.create_pool_async(
                user=self.config.ro_user,
                password=self.config.ro_password,
                dsn=self.config.dsn,
                min=1,
                max=self.config.pool_max,
                increment=self.config.pool_increment,
            )

    async def close(self) -> None:
        """커넥션 풀 종료 (앱 종료 시 호출)"""
        if self._pool:
            await self._pool.close()
            logger.info("Oracle 연결 풀 종료됨")
        if self._ro_pool:
            await self._ro_pool.close()
            logger.info("Oracle 읽기 전용 연결 풀 종료됨")

    @asynccontextmanager
    async def connection(
        self, readonly: bool = False
    ) -> AsyncIterator[oracledb.AsyncConnection]:
        """커넥션 풀에서 연결을 하나 획득하여 컨텍스트 매니저로 반환

        Args:
            readonly: True 이면 읽기 전용 계정 풀 사용.
                      읽기 전용 계정이 없으면 기본 풀에서 SET TRANSACTION READ ONLY 실행.

        Yields:
            oracledb.AsyncConnection: 비동기 Oracle 연결 객체

        Raises:
            RuntimeError: start() 가 호출되지 않은 경우
        """
        if self._pool is None:
            raise RuntimeError("OracleDB.start() 를 먼저 호출해야 합니다")

        if readonly and self._ro_pool:
            pool = self._ro_pool
        else:
            pool = self._pool

        conn: oracledb.AsyncConnection = await pool.acquire()
        try:
            if readonly and not self._ro_pool:
                cur = conn.cursor()
                await cur.execute("SET TRANSACTION READ ONLY")
            yield conn
            if readonly and not self._ro_pool:
                cur = conn.cursor()
                await cur.execute("COMMIT")
        except Exception:
            await conn.rollback()
            raise
        finally:
            await pool.release(conn)

    async def fetch_all(
        self, sql: str, params: dict | None = None, readonly: bool = False
    ) -> list[dict]:
        """SELECT 쿼리를 실행하고 결과를 dict 리스트로 반환"""
        async with self.connection(readonly=readonly) as conn:
            cursor = conn.cursor()
            await cursor.execute(sql, params or {})
            rows = await cursor.fetchall()
            columns = [col[0].lower() for col in (cursor.description or [])]
            return [dict(zip(columns, row)) for row in rows]

    async def execute(
        self, sql: str, params: dict | None = None
    ) -> int:
        """INSERT/UPDATE/DELETE 쿼리를 실행하고 영향받은 행 수를 반환"""
        async with self.connection() as conn:
            cursor = conn.cursor()
            await cursor.execute(sql, params or {})
            await conn.commit()
            return cursor.rowcount

    @property
    def is_ready(self) -> bool:
        return self._pool is not None
