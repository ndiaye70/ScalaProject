create table dossiers_medicaux
(
    id                   bigserial
        primary key,
    patient_id           bigint    not null
        constraint patient_fk
            references patients
            on delete cascade,
    date_creation        timestamp not null,
    antecedents          varchar   not null,
    allergies            varchar   not null,
    traitements_en_cours varchar   not null,
    notes                varchar   not null
);

alter table dossiers_medicaux
    owner to postgres;

