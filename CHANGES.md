Change Log
==========

### 4.3.1 - tba

#### Miscellaneous
* Updated all dependencies to their latest versions.

### 4.3.0 - 2024-09-19

#### Changes
* **Breaking:** Java 11 is now the minimum required version for using the Spreadsheet Generator Plugin.
* Updated to [Importer/Exporter](https://github.com/3dcitydb/importer-exporter) version 5.5.0.

#### Miscellaneous
* Updated all dependencies to their latest versions.

### 4.2.0 - 2022-12-15

* Updated to [Importer/Exporter](https://github.com/3dcitydb/importer-exporter) version 5.3.0.
* Updated Apache POI to 5.2.3.

### 4.1.0 - 2022-05-23

#### Changes
* Use the new default view implementations of the Plugin API introduced with **version 5.2.0** of the
  [Importer/Exporter](https://github.com/3dcitydb/importer-exporter).

#### Fixes
* Fixed plugin description where the `<ade-support>` should be true.

### 4.0.1 - 2022-02-07

#### Changes
* Removed the hardcoded hyperlink of the online documentation from the plugin description.

### 4.0.0 - 2021-10-08

#### Breaking changes
* This version is implemented against the new Plugin API introduced with **version 5.0.0** of the
  [Importer/Exporter](https://github.com/3dcitydb/importer-exporter). It *cannot be used with previous versions*
  of the Importer/Exporter anymore.

### 3.2.0 - 2021-04-28

#### Additions
* Added support for exporting attributes of features defined in a CityGML ADE if a corresponding ADE extension
  has been registered with the 3D City Database and the Importer/Exporter. [#4](https://github.com/3dcitydb/plugin-spreadsheet-generator/pull/4)
* The plugin can now be used from the command-line. For this purpose, it adds the `export-table` command to
  the command-line interface of the Importer/Exporter. [#5](https://github.com/3dcitydb/plugin-spreadsheet-generator/pull/5)
* Added new filter options like an SQL filer to better restrict the export to a subset of the features stored
  in the 3DCityDB. 
* Updated the graphical user interface to the new look&feel of the Importer/Exporter. [#6](https://github.com/3dcitydb/plugin-spreadsheet-generator/pull/6)
* Completely updated user manual at https://3dcitydb-docs.readthedocs.io/en/release-v4.3.0/

#### Changes
* Using a bounding box filter is not mandatory for the export anymore.

#### Miscellaneous
* This version works with version 4.3.x of the [3D City Database Importer/Exporter](https://github.com/3dcitydb/importer-exporter)