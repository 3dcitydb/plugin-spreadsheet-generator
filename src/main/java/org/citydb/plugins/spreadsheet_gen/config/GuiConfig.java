package org.citydb.plugins.spreadsheet_gen.config;

import org.citydb.config.gui.components.SQLExportFilterComponent;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "GuiConfigType", propOrder = {
		"collapseFeatureVersionFilter",
		"collapseAttributeFilter",
		"collapseSQLFilter",
		"collapseBoundingBoxFilter",
		"collapseFeatureTypeFilter",
		"showUnsupportedADEWarning",
		"sqlFilterComponent"
})
public class GuiConfig {
	private boolean collapseFeatureVersionFilter = false;
	private boolean collapseAttributeFilter = true;
	private boolean collapseSQLFilter = true;
	private boolean collapseBoundingBoxFilter = true;
	private boolean collapseFeatureTypeFilter = true;
	private boolean showUnsupportedADEWarning = true;
	private SQLExportFilterComponent sqlFilterComponent;

	public GuiConfig() {
		sqlFilterComponent = new SQLExportFilterComponent();
	}

	public boolean isCollapseFeatureVersionFilter() {
		return collapseFeatureVersionFilter;
	}

	public void setCollapseFeatureVersionFilter(boolean collapseFeatureVersionFilter) {
		this.collapseFeatureVersionFilter = collapseFeatureVersionFilter;
	}

	public boolean isCollapseAttributeFilter() {
		return collapseAttributeFilter;
	}

	public void setCollapseAttributeFilter(boolean collapseAttributeFilter) {
		this.collapseAttributeFilter = collapseAttributeFilter;
	}

	public boolean isCollapseSQLFilter() {
		return collapseSQLFilter;
	}

	public void setCollapseSQLFilter(boolean collapseSQLFilter) {
		this.collapseSQLFilter = collapseSQLFilter;
	}

	public boolean isCollapseBoundingBoxFilter() {
		return collapseBoundingBoxFilter;
	}

	public void setCollapseBoundingBoxFilter(boolean collapseBoundingBoxFilter) {
		this.collapseBoundingBoxFilter = collapseBoundingBoxFilter;
	}

	public boolean isCollapseFeatureTypeFilter() {
		return collapseFeatureTypeFilter;
	}

	public void setCollapseFeatureTypeFilter(boolean collapseFeatureTypeFilter) {
		this.collapseFeatureTypeFilter = collapseFeatureTypeFilter;
	}

	public boolean isShowUnsupportedADEWarning() {
		return showUnsupportedADEWarning;
	}

	public void setShowUnsupportedADEWarning(boolean showUnsupportedADEWarning) {
		this.showUnsupportedADEWarning = showUnsupportedADEWarning;
	}

	public SQLExportFilterComponent getSQLExportFilterComponent() {
		return sqlFilterComponent;
	}

	public void setSQLExportFilterComponent(SQLExportFilterComponent sqlFilterComponent) {
		if (sqlFilterComponent != null) {
			this.sqlFilterComponent = sqlFilterComponent;
		}
	}
}
