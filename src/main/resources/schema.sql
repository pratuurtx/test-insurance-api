CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE status_enum AS ENUM ('ACTIVE', 'INACTIVE');
CREATE TYPE type_enum AS ENUM ('BANNER', 'PROMOTION', 'INSURANCE', 'SUIT_INSURANCE');

CREATE TABLE IF NOT EXISTS contents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    effective_from  TIMESTAMP NOT NULL,
    effective_to TIMESTAMP NOT NULL,
    status status_enum NOT NULL,
    type type_enum NOT NULL
    );

CREATE TABLE IF NOT EXISTS banners (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cover_image_path VARCHAR(255) NOT NULL,
    cover_hyper_link VARCHAR(255) NOT NULL,
    content_id UUID NOT NULL UNIQUE,
    CONSTRAINT fk_banner_content FOREIGN KEY (content_id)
    REFERENCES contents(id)
    );

CREATE TABLE IF NOT EXISTS banner_contents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    banner_id UUID NOT NULL,
    content_image_path VARCHAR(255) NOT NULL,
    content_hyper_link VARCHAR(255) NOT NULL,
    CONSTRAINT fk_banner_content_banner FOREIGN KEY (banner_id)
    REFERENCES banners(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS suit_insurances (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title_th VARCHAR(255) NOT NULL,
    title_en VARCHAR(255) NOT NULL,
    image_path VARCHAR(255) NOT NULL,
    content_id UUID NOT NULL UNIQUE,
    CONSTRAINT fk_suit_insurance_content FOREIGN KEY (content_id)
    REFERENCES contents(id)
    );

CREATE TABLE IF NOT EXISTS insurances (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    icon_image_path VARCHAR(255) NOT NULL,
    cover_image_path VARCHAR(255) NOT NULL,
    title_th VARCHAR(255) NOT NULL,
    title_en VARCHAR(255) NOT NULL,
    description_th TEXT NOT NULL,
    description_en TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    content_id UUID NOT NULL UNIQUE,
    CONSTRAINT fk_insurance_content FOREIGN KEY (content_id)
    REFERENCES contents(id)
    );

CREATE TABLE IF NOT EXISTS promotions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cover_image_path VARCHAR(255) NOT NULL,
    title_th VARCHAR(255) NOT NULL,
    title_en VARCHAR(255) NOT NULL,
    description_th TEXT NOT NULL,
    description_en TEXT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    content_id UUID NOT NULL UNIQUE,
    CONSTRAINT fk_promotion_content FOREIGN KEY (content_id)
    REFERENCES contents(id)
    );

CREATE INDEX IF NOT EXISTS idx_banner_content_banner_id ON banner_contents (banner_id);
CREATE INDEX IF NOT EXISTS idx_insurance_deleted_at ON insurances (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_promotion_deleted_at ON promotions (deleted_at) WHERE deleted_at IS NULL;
