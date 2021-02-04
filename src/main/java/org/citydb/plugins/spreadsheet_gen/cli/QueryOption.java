package org.citydb.plugins.spreadsheet_gen.cli;

import org.citydb.config.geometry.BoundingBox;
import org.citydb.config.project.query.filter.type.FeatureTypeFilter;
import org.citydb.plugin.cli.CliOption;
import org.citydb.plugin.cli.CliOptionBuilder;
import org.citydb.plugin.cli.TypeNamesOption;
import picocli.CommandLine;

public class QueryOption implements CliOption {
    @CommandLine.ArgGroup (exclusive = false)
    private TypeNamesOption typeNamesOption;

    @CommandLine.Option(names = {"-b", "--bbox"}, paramLabel = "<minx,miny,maxx,maxy[,srid]>",
            description = "Bounding box to use as spatial filter.")
    private String bbox;

    private BoundingBox boundingBox;

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public FeatureTypeFilter getFeatureTypeFilter() {
        return typeNamesOption != null ? typeNamesOption.toFeatureTypeFilter() : null;
    }

    @Override
    public void preprocess(CommandLine commandLine) throws Exception {
        if (typeNamesOption != null) {
            typeNamesOption.preprocess(commandLine);
        }

        if (bbox != null) {
            boundingBox = CliOptionBuilder.boundingBox(bbox, commandLine);
        }
    }
}
