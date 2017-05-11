REF: File attachments attributes

Two new attribute types have been added to facilitate file attachments functionality.

File - this is an arbitrary file that can be attached to attributable objects much like the Image attributes.
New FileFilter is added that allows to download these files from storefront using the following /filevault/[entity]/[filename]
The entity types are same as with Image attributes (product, brand, category and shop). The file name follows same
naming convention as images: [name]_[code]_[suffix].[extension].

SystemFile - this attribute defines also a file attachment which is not available in the storefront. It can be used to facilitate
files which are required for integrations, such as payment gateway certificate files.

MAJOR CHANGES
- Two new types of attributes are added: File and SystemFile.
- New attribute codes are added but it is possible to add more such attributes in the same fashion as the image atttributes.
- Two new repository location configurations are added: filevault and sysfilevault
- FileFilter in storefronts is able to server publicly File type attribute values from the filevault

CHANGES TO PROD SYSTEMS
1. Configure two new locations in system preferences for: filevault and sysfilevault
2. Optionaly add more file attributes if needed.

