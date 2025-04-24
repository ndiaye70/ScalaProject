create table gardes
(
    id                 bigserial
        primary key,
    personnel_id       bigint not null
        constraint personnel_fk
            references personnels,
    date_debut_periode date   not null,
    date_fin_periode   date   not null,
    heure_debut        time   not null,
    heure_fin          time   not null
);

alter table gardes
    owner to postgres;

