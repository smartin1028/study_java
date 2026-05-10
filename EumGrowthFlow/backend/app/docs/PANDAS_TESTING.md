# pandas + DB 통합 테스트 가이드

pandas DataFrame 을 활용한 DB 연동 및 테스트 패턴 정리.

테스트 파일: `tests/test_pandas.py`

---

## 1. DataFrame 기본 조작

```python
import pandas as pd

def test_dataframe():
    df = pd.DataFrame([
        {"id": 1, "name": "Alice", "age": 30},
        {"id": 2, "name": "Bob", "age": 25},
        {"id": 3, "name": "Charlie", "age": 35},
    ])
    assert df.shape == (3, 3)          # (row, col)
    assert list(df.columns) == ["id", "name", "age"]
    assert df["name"].tolist() == ["Alice", "Bob", "Charlie"]
```

## 2. 필터링 & 집계 (SQL 비교)

```python
# SQL: SELECT * FROM df WHERE score >= 80
high = df[df["score"] >= 80]

# SQL: SELECT dept, AVG(salary) FROM df GROUP BY dept
agg = df.groupby("dept")["salary"].mean()
```

## 3. DB 결과 → DataFrame

`OracleDB.fetch_all()` 이 `list[dict]` 를 반환하므로 `pd.DataFrame()` 에 바로 넣을 수 있다.

```python
rows = await db.fetch_all("SELECT id, name, dept FROM users")
df = pd.DataFrame(rows)

# 컬럼 순서 지정
df = pd.DataFrame(rows, columns=["id", "name", "dept"])
```

## 4. Mock DB → DataFrame → 가공

```python
@pytest.mark.asyncio
async def test_db_to_df():
    db = AsyncMock()
    db.fetch_all.return_value = [
        {"product": "A", "qty": 10, "price": 100},
        {"product": "B", "qty": 5, "price": 200},
    ]

    rows = await db.fetch_all("SELECT ...")
    df = pd.DataFrame(rows)
    df["amount"] = df["qty"] * df["price"]

    assert df["amount"].sum() == 2000  # 10*100 + 5*200
```

## 5. DataFrame → DB INSERT

```python
@pytest.mark.asyncio
async def test_df_to_db():
    df = pd.DataFrame([
        {"id": 1, "name": "Alice", "score": 95},
        {"id": 2, "name": "Bob", "score": 88},
    ])

    db = AsyncMock()
    db.execute.return_value = 1

    for _, row in df.iterrows():
        await db.execute(
            "INSERT INTO students VALUES (:id, :name, :score)",
            row.to_dict(),
        )

    assert db.execute.await_count == 2
```

## 6. pivot_table (엑셀 피벗)

```python
# SQL:
#   SELECT month, category, SUM(sales) FROM report
#   GROUP BY month, category
# pivot_table 은 엑셀 피벗처럼 2차원 요약을 만든다.

pivot = df.pivot_table(
    values="sales", index="month", columns="category", aggfunc="sum"
)
assert pivot.loc["2026-01", "A"] == 100
```

## 7. CSV → pandas → DB 파라미터

```python
def test_csv_to_db(tmp_path):
    csv = tmp_path / "users.csv"
    csv.write_text("name,email\nAlice,a@t.com\nBob,b@t.com")

    df = pd.read_csv(csv)
    # to_dict(orient="records") → INSERT 용 list[dict]
    records = df.to_dict(orient="records")
    # [{"name": "Alice", "email": "a@t.com"}, {"name": "Bob", "email": "b@t.com"}]
```

`to_dict()` orient 옵션:

| orient | 결과 | 용도 |
|---|---|---|
| `"records"` | `[{col: val, ...}, ...]` | DB INSERT 파라미터 |
| `"list"` | `{col: [val, ...], ...}` | 컬럼별 리스트 추출 |
| `"dict"` | `{index: {col: val, ...}, ...}` | 인덱스 기준 매핑 |
| `"index"` | `{index: {col: val, ...}, ...}` | 인덱스 기준 (dict 와 유사) |

## 8. JSON → DataFrame

```python
def test_json_to_df():
    data = [
        {"date": "2026-01-01", "revenue": 1000},
        {"date": "2026-01-02", "revenue": 1500},
    ]
    df = pd.DataFrame(data).sort_values("revenue", ascending=False)
    assert df.iloc[0]["revenue"] == 1500
```

## 9. OracleDB + pandas 통합 Mock 흐름

```python
@pytest.mark.asyncio
async def test_oracle_to_pivot_flow():
    db = AsyncMock()
    db.fetch_all.return_value = [
        {"month": "2026-01", "category": "A", "sales": 100},
        {"month": "2026-01", "category": "B", "sales": 200},
        {"month": "2026-02", "category": "A", "sales": 150},
        {"month": "2026-02", "category": "B", "sales": 250},
    ]

    rows = await db.fetch_all("SELECT ...", readonly=True)
    df = pd.DataFrame(rows)

    # pivot 분석
    pivot = df.pivot_table(values="sales", index="month",
                           columns="category", aggfunc="sum")

    # 월별 합계
    monthly = df.groupby("month")["sales"].sum().to_dict()
    assert monthly["2026-01"] == 300
```

---

## pandas ↔ SQL 치트시트

| SQL | pandas |
|---|---|
| `WHERE score >= 80` | `df[df["score"] >= 80]` |
| `SELECT name, age` | `df[["name", "age"]]` |
| `GROUP BY dept` | `df.groupby("dept")` |
| `AVG(salary)` | `.mean()` |
| `SUM(sales)` | `.sum()` |
| `COUNT(*)` | `.size()` / `.count()` |
| `ORDER BY col DESC` | `df.sort_values("col", ascending=False)` |
| `LIMIT 5` | `df.head(5)` |
| `SELECT DISTINCT dept` | `df["dept"].unique()` |
| `INSERT INTO ... VALUES` | `df.iterrows()` + `row.to_dict()` |
