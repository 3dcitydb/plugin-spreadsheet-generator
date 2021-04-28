Change Log
==========

### 3.2.0

##### Additions
* Added support for exporting attributes of features defined in a CityGML ADE if a corresponding ADE extension
  has been registered with the 3D City Database and the Importer/Exporter. [#4](https://github.com/3dcitydb/plugin-spreadsheet-generator/pull/4)
* The plugin can now be used from the command-line. For this purpose, it adds the `export-table` command to
  the command-line interface of the Importer/Exporter. [#5](https://github.com/3dcitydb/plugin-spreadsheet-generator/pull/5)
* Added new filter options like an SQL filer to better restrict the export to a subset of the features stored
  in the 3DCityDB. 
* Updated the graphical user interface to the new look&feel of the Importer/Exporter. [#6](https://github.com/3dcitydb/plugin-spreadsheet-generator/pull/6)
* Completely updated user manual at https://3dcitydb-docs.readthedocs.io/en/release-v4.3.0/

##### Changes
* Using a bounding box filter is not mandatory for the export anymore.

##### Miscellaneous
* This version works with version 4.3.x of the [3D City Database Importer/Exporter](https://github.com/3dcitydb/importer-exporter)