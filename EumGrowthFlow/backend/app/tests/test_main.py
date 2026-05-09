def test_valid_bearer_token(client):
    response = client.post(
        "/llm",
        json={"prompt": "Hello, world!"},
        headers={"Authorization": "Bearer test_token"},
    )
    print(response.text)
    assert response.status_code == 200
    data = response.json()
    assert data["response"] == "Mocked response"
    assert data["model"] == "test-model"


def test_invalid_bearer_token(client):
    response = client.post(
        "/llm",
        json={"prompt": "Hello, world!"},
        headers={"Authorization": "Bearer wrong_token"},
    )
    assert response.status_code == 403
    assert response.json()["detail"] == "Invalid bearer token"


def test_missing_bearer_token(client):
    response = client.post(
        "/llm",
        json={"prompt": "Hello, world!"},
    )
    assert response.status_code == 401
    assert response.json()["detail"] == "Invalid or missing bearer token"
