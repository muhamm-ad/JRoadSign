# JRoadSign

**JRoadSign** is a Java library designed to represent and process road signs as Java objects. This tool transforms road
sign data from various formats into structured Java objects, providing a flexible and powerful interface for managing
road sign information across diverse Java applications.

## Features

- **Flexible Data Handling**: Capable of processing road sign data from various sources, including but not limited to
  GeoJSON, tailored to the specific requirements of each city module.
- **Comprehensive Data Access**: Offers extensive functions for accessing a wide range of road sign properties, such as
  schedules, restrictions, and geographical coordinates.
- **Modular City-Specific Design**: Structured into city-specific sub-modules, each tailored to meet the unique road
  sign configurations of individual cities.

## Supported Cities

Currently, **JRoadSign** includes modules for the following cities:

- [Montreal](docs/canada/quebec/montreal) - Utilizes data sourced from GeoJSON or CSV files
  from [Montreal Open Data](https://donnees.montreal.ca/dataset/).

Plans are underway to expand support to additional cities, incorporating various data sources and structures.

## Getting Started

### Prerequisites

- Java JDK (Version 17 or later recommended). Available for download
  at [Oracle Java SE Downloads](https://www.oracle.com/java/technologies/javase-downloads.html).

### Usage

1. **Module Integration**: Incorporate the `JRoadSign` module into your Java project. For city-specific data, import the
   respective city sub-module (e.g., `canada/quebec/montreal`).
2. **Data Interaction**: Utilize the functions provided in each sub-module to load and manage road sign data, tailored
   to the specific format and structure of the city.

#### Example

```java
import org.jroadsign.canada.quebec.montreal.RoadParkingSign;
import org.jroadsign.canada.quebec.montreal.RoadPost;

import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        RoadParkingSign roadParkingSign = new RoadParkingSign("path_to/Signalisation-Stationnement.geojson");
        // Further interaction with data...
    }
}
```

## Contributing

Contributions to JRoadSign are highly appreciated. You can assist in various ways, including reporting bugs, suggesting
enhancements, adding new city modules, or improving documentation. For more detailed information, see
our [contribution guidelines](docs/CONTRIBUTING.md). Feel free to open issues or submit pull requests on our GitHub
repository.

## Authors

- [muhamm-ad](https://github.com/muhamm-ad) - Project Founder and Main Contributor.