CREATE TABLE IF NOT EXISTS public.vector_store (
    id VARCHAR(255) PRIMARY KEY,
    content TEXT NOT NULL,
    metadata JSONB,
    embedding VECTOR(1024)
);
-- 1024是因为配置的嵌入模型目前支持1024维度