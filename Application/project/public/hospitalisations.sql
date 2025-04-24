create table hospitalisations
(
    id                       bigserial
        primary key,
    patient_id               bigint    not null
        constraint fk_hospitalisation_patient
            references patients,
    lit_id                   bigint    not null
        constraint fk_hospitalisation_lit
            references lits,
    date_debut               timestamp not null,
    date_fin                 timestamp,
    motif                    varchar,
    personnel_responsable_id bigint
        constraint fk_hospitalisation_personnel
            references personnels,
    notes                    varchar
);

alter table hospitalisations
    owner to postgres;

