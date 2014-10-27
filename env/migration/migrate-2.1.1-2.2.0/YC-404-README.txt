REF: YC-404	Create Image Import Descriptor

This task also included some refactoring and review of the existing code. Some of this work
related to resolution of the import directory location. Due to the fact that it was configured
in several places and thus made upload-import/descriptor-file/location mechanism brittle.
Refactoring unified this to have configuration for import directory discovery by ImportDirectorService
only that distributes this configuration to upload service and all descriptors.

Actions for migration:

1. Remove "import-directory" tag from all import descriptors.