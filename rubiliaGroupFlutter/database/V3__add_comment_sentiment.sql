ALTER TABLE comments ADD COLUMN sentiment VARCHAR(20);
ALTER TABLE comments ADD COLUMN sentiment_score DOUBLE;
ALTER TABLE comments ADD COLUMN sentiment_explanation TEXT;
ALTER TABLE comments ADD COLUMN analyzed_at TIMESTAMP NULL;
