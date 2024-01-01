# JRoadSign Montreal Module

This is the Montreal submodule of the **JRoadSign** project, a specialized module for processing and representing
Montreal's road signs as Java objects. It focuses on handling data specifically for Montreal, primarily sourced from
GeoJSON files.

## Features

- **GeoJSON Data Processing**: Optimized for converting GeoJSON data of Montreal's road signs into Java objects.
- **Customized Data Access**: Functions tailored to the unique characteristics of Montreal's road signs, including
  specific schedules, restrictions, and geographical coordinates.

## Data Source

For the Montreal module, road sign data is primarily obtained from GeoJSON files. This allows for accurate and
comprehensive representation of the city's road signs in a structured format.

### Installation and Usage

1. **Module Integration**: Incorporate the Montreal submodule into your Java project alongside the main JRoadSign
   library.
2. **Data Loading**: Use the specific functions provided in this module to load and process road sign data for Montreal.

#### Example

```java
import org.JRoadSign.quebec.montreal;

// Example of loading Montreal road sign data from a GeoJSON file
List<RoadSign> montrealRoadSigns = montreal.loadDataFunction("path_to_your_geojson_file");
```

## Contributing

Contributions specifically to the Montreal module of JRoadSign are welcome. If you have suggestions, bug reports, or
enhancements, please feel free to open an issue or a pull request on the main JRoadSign repository, specifying that it's
for the Montreal submodule.

## Module Maintainers

- [muhamm-ad](https://github.com/muhamm-ad) - Module Lead and Primary Contributor.

## License

This module is part of the JRoadSign project, which is licensed under the [GPL-3.0](../../../../../../../docs/LICENSE).