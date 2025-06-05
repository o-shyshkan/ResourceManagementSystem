--liquibase formatted sql
--changeset alex:4
DROP TABLE IF EXISTS public.resources_characteristics;
CREATE TABLE IF NOT EXISTS public.resources_characteristics
(
    characteristic_id bigint NOT NULL,
    resource_id bigint NOT NULL,
    CONSTRAINT fk6ewru1xu6528sy6mj0fk3sobd FOREIGN KEY (resource_id)
        REFERENCES public.resources (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk8v8g811x8uqoguffsqhm3lj1v FOREIGN KEY (characteristic_id)
        REFERENCES public.characteristics (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.resources_characteristics
    OWNER to sa;
