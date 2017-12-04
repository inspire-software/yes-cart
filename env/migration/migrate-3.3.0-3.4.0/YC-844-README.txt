REF: YC-844 INSERT_ONLY and UPDATE_ONLY import modes

Two new import modes has been added to the import-descriptor:

- INSERT_ONLY: this mode is used restrict to inserting new records only (existing records are skipped, even if the
  values do not match)

- UPDATE_ONLY: this mode is used to restrict to updating old records only (inexistent records are skipped).

Note that these two modes are different to <insert-only>, <update-only> or <update-skip-unresolved> which are
driven by a property value (i.e. single column level). The mode is acting on the tuple level.

Changes to PROD:

- all import descriptors that use <insert-sql> have to have INSERT_ONLY mode explicitly set (this was enforces to
  remove ambiguity)
