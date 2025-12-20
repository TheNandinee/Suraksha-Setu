from fastapi import FastAPI
from pydantic import BaseModel
from detoxify import Detoxify

app = FastAPI()
model = Detoxify('original')  # first load may download weights

class TextPayload(BaseModel):
    text: str

@app.post("/score")
async def score(payload: TextPayload):
    if not payload.text:
        return {}
    results = model.predict(payload.text)
    return results
