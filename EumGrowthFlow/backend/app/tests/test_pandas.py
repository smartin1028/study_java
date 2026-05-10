"""
pandas + DB 통합 테스트 예제

DataFrame 기본 조작부터 OracleDB 연동까지 실전 패턴을 익힌다.

실행:
    pytest tests/test_pandas.py -v
"""

from unittest.mock import AsyncMock

import pandas as pd
import pytest


# ============================================================
# 1. DataFrame 기본 조작
# ============================================================

def test_dataframe_creation():
    df = pd.DataFrame([
        {"id": 1, "name": "Alice", "age": 30},
        {"id": 2, "name": "Bob", "age": 25},
        {"id": 3, "name": "Charlie", "age": 35},
    ])
    assert df.shape == (3, 3)
    assert list(df.columns) == ["id", "name", "age"]
    assert df["name"].tolist() == ["Alice", "Bob", "Charlie"]


def test_dataframe_filter():
    """pandas filter == SQL WHERE"""
    df = pd.DataFrame([
        {"name": "Alice", "score": 85},
        {"name": "Bob", "score": 72},
        {"name": "Charlie", "score": 90},
        {"name": "Diana", "score": 68},
    ])
    high = df[df["score"] >= 80]
    assert len(high) == 2
    assert high["name"].tolist() == ["Alice", "Charlie"]


def test_dataframe_aggregate():
    """pandas groupby == SQL GROUP BY"""
    df = pd.DataFrame([
        {"dept": "ENG", "salary": 5000},
        {"dept": "ENG", "salary": 6000},
        {"dept": "HR", "salary": 4000},
        {"dept": "HR", "salary": 4500},
    ])
    agg = df.groupby("dept")["salary"].mean().to_dict()
    assert agg["ENG"] == 5500.0
    assert agg["HR"] == 4250.0


# ============================================================
# 2. DB 쿼리 결과 → pandas DataFrame
# ============================================================

@pytest.mark.asyncio
async def test_db_result_to_dataframe():
    """OracleDB.fetch_all() 결과(list[dict])를 DataFrame 으로 변환"""
    rows: list[dict] = [
        {"id": 1, "name": "Alice", "dept": "ENG"},
        {"id": 2, "name": "Bob", "dept": "HR"},
        {"id": 3, "name": "Charlie", "dept": "ENG"},
    ]
    df = pd.DataFrame(rows)
    assert df.shape == (3, 3)
    assert df["name"].tolist() == ["Alice", "Bob", "Charlie"]

    # 컬럼 순서 지정
    df2 = pd.DataFrame(rows, columns=["id", "name", "dept"])
    assert list(df2.columns) == ["id", "name", "dept"]


@pytest.mark.asyncio
async def test_mock_fetch_all_to_dataframe():
    """Mock DB → DataFrame → 가공 → 검증 전체 흐름"""
    db = AsyncMock()
    db.fetch_all.return_value = [
        {"product": "A", "qty": 10, "price": 100},
        {"product": "B", "qty": 5, "price": 200},
        {"product": "C", "qty": 20, "price": 50},
    ]

    rows = await db.fetch_all("SELECT product, qty, price FROM orders")
    df = pd.DataFrame(rows)
    df["amount"] = df["qty"] * df["price"]

    assert df["amount"].sum() == 3000  # 10*100 + 5*200 + 20*50
    assert df.shape == (3, 4)


# ============================================================
# 3. pandas → DB batch INSERT
# ============================================================
#
# @pytest.mark.asyncio
# async def test_dataframe_to_db_insert():
#     """DataFrame 데이터를 DB 에 배치 INSERT"""
#     df = pd.DataFrame([
#         {"id": 1, "name": "Alice", "score": 95},
#         {"id": 2, "name": "Bob", "score": 88},
#     ])
#
#     db = AsyncMock()
#     db.execute.return_value = 1
#
#     inserted = 0
#     for _, row in df.iterrows():
#         await db.execute(
#             "INSERT INTO students VALUES (:id, :name, :score)",
#             row.to_dict(),
#         )
#         inserted += 1
#
#     assert inserted == 2
#     assert db.execute.await_count == 2
#

# ============================================================
# 4. CSV/JSON 파일 → pandas → DB
# ============================================================

def test_csv_to_dataframe_to_db_format(tmp_path):
    """CSV 읽기 → DataFrame → INSERT 용 dict 리스트 변환"""
    csv_file = tmp_path / "users.csv"
    csv_file.write_text(
        "name,email,age\n"
        "Alice,alice@test.com,30\n"
        "Bob,bob@test.com,25\n"
    )

    df = pd.read_csv(csv_file)
    assert len(df) == 2

    records = df.to_dict(orient="records")
    assert records == [
        {"name": "Alice", "email": "alice@test.com", "age": 30},
        {"name": "Bob", "email": "bob@test.com", "age": 25},
    ]


def test_json_to_dataframe():
    """JSON → DataFrame → 정렬 분석"""
    json_data = [
        {"date": "2026-01-01", "revenue": 1000},
        {"date": "2026-01-02", "revenue": 1500},
        {"date": "2026-01-03", "revenue": 800},
    ]

    df = pd.DataFrame(json_data)
    df = df.sort_values("revenue", ascending=False)

    assert df.iloc[0]["date"] == "2026-01-02"
    assert df.iloc[0]["revenue"] == 1500


# ============================================================
# 5. OracleDB + pandas 통합 Mock 흐름
# ============================================================

@pytest.mark.asyncio
async def test_oracle_fetch_to_dataframe_flow():
    """OracleDB → DataFrame → pivot + groupby 통합 분석"""
    db = AsyncMock()
    db.fetch_all.return_value = [
        {"month": "2026-01", "category": "A", "sales": 100},
        {"month": "2026-01", "category": "B", "sales": 200},
        {"month": "2026-02", "category": "A", "sales": 150},
        {"month": "2026-02", "category": "B", "sales": 250},
    ]

    rows = await db.fetch_all(
        "SELECT month, category, sales FROM monthly_report",
        readonly=True,
    )

    df = pd.DataFrame(rows)

    # 엑셀 피벗처럼 2차원 집계
    pivot = df.pivot_table(
        values="sales", index="month", columns="category", aggfunc="sum"
    )
    assert pivot.loc["2026-01", "A"] == 100
    assert pivot.loc["2026-02", "B"] == 250

    # 월별 합계
    monthly_total = df.groupby("month")["sales"].sum().to_dict()
    assert monthly_total["2026-01"] == 300
    assert monthly_total["2026-02"] == 400
