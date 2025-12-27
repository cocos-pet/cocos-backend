CREATE INDEX idx_symptom_body_id ON symptom (body_id);
CREATE INDEX idx_review_symptom_symptom_id_review_id ON review_symptom (symptom_id, review_id);
CREATE INDEX idx_review_summary_review_summary_option_id_review_id ON review_summary (review_summary_option_id, review_id);