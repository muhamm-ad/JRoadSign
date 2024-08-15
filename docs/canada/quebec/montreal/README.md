# JRoadSign Montreal Module

This is the Montréal submodule of the **JRoadSign** project, a specialized module for processing and representing
Montréal's road signs as Java objects.
It focuses on handling data specifically for Montréal, sourced
from [Montreal Open Data](https://donnees.montreal.ca/dataset/).

## Data Source

Data Source: [Montreal Open Data](https://donnees.montreal.ca/dataset/)
For the Montreal module, road sign data is obtained from GeoJSON files. This allows for accurate and
comprehensive representation of the city's road signs in a structured format.

### Installation and Usage

1. **Module Integration**: Incorporate the Montreal submodule into your Java project alongside the main JRoadSign
   library.
2. **Data Loading**: Use the specific functions provided in this module to load and process road sign data for Montreal.

#### Example

```java
import org.jroadsign.canada.quebec.montreal.RoadParkingSign;

RoadParkingSign roadParkingSign = new RoadParkingSign("path_to_your_file.geojson");
```
## Contributing

Contributions specifically to the Montreal module of JRoadSign are welcome. If you have suggestions, bug reports, or
enhancements, please feel free to open an issue or a pull request on the montréal branch.

## Module Maintainers

- [muhamm-ad](https://github.com/muhamm-ad) - Module Lead and Primary Contributor.