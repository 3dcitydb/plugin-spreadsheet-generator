package org.citydb.plugins.spreadsheet_gen.cli;

import org.citydb.cli.options.vis.BoundingBoxOption;
import org.citydb.config.project.query.filter.selection.id.ResourceIdOperator;
import org.citydb.config.project.query.filter.selection.sql.SelectOperator;
import org.citydb.config.project.query.simple.SimpleAttributeFilter;
import org.citydb.config.project.query.simple.SimpleFeatureVersionFilter;
import org.citydb.plugin.cli.CliOption;
import org.citydb.plugin.cli.FeatureVersionOption;
import org.citydb.plugin.cli.ResourceIdOption;
import org.citydb.plugin.cli.SQLSelectOption;
import org.citydb.plugin.cli.TypeNamesOption;
import org.citydb.plugins.spreadsheet_gen.config.SimpleQuery;
import org.citydb.registry.ObjectRegistry;
import picocli.CommandLine;

import javax.xml.datatype.DatatypeFactory;

public class QueryOption implements CliOption {
    @CommandLine.ArgGroup (exclusive = false)
    private TypeNamesOption typeNamesOption;

    @CommandLine.ArgGroup(exclusive = false)
    private FeatureVersionOption featureVersionOption;

    @CommandLine.ArgGroup
    private ResourceIdOption resourceIdOption;

    @CommandLine.ArgGroup
    private BoundingBoxOption boundingBoxOption;

    @CommandLine.ArgGroup
    private SQLSelectOption sqlSelectOption;

    public SimpleQuery toQuery() {
        SimpleQuery query = new SimpleQuery();

        if (typeNamesOption != null) {
            query.setUseTypeNames(true);
            query.setFeatureTypeFilter(typeNamesOption.toFeatureTypeFilter());
        }

        if (featureVersionOption != null) {
            DatatypeFactory datatypeFactory = ObjectRegistry.getInstance().getDatatypeFactory();
            SimpleFeatureVersionFilter versionFilter = featureVersionOption.toFeatureVersionFilter(datatypeFactory);
            if (versionFilter != null) {
                query.setUseFeatureVersionFilter(true);
                query.setFeatureVersionFilter(versionFilter);
            }
        }

        if (resourceIdOption != null) {
            ResourceIdOperator idOperator = resourceIdOption.toResourceIdOperator();
            if (idOperator != null) {
                query.setUseAttributeFilter(true);
                SimpleAttributeFilter attributeFilter = new SimpleAttributeFilter();
                attributeFilter.setResourceIdFilter(idOperator);
                query.setAttributeFilter(attributeFilter);
            }
        }

        if (boundingBoxOption != null) {
            query.setUseBboxFilter(true);
            query.setBboxFilter(boundingBoxOption.toBoundingBox());
        }

        if (sqlSelectOption != null) {
            SelectOperator selectOperator = sqlSelectOption.toSelectOperator();
            if (selectOperator != null) {
                query.setUseSQLFilter(true);
                query.setSQLFilter(selectOperator);
            }
        }

        return query;
    }

    @Override
    public void preprocess(CommandLine commandLine) throws Exception {
        if (typeNamesOption != null) {
            typeNamesOption.preprocess(commandLine);
        }

        if (featureVersionOption != null) {
            featureVersionOption.preprocess(commandLine);
        }

        if (boundingBoxOption != null) {
            boundingBoxOption.preprocess(commandLine);
        }
    }
}
