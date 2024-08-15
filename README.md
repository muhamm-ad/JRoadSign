# JRoadSign

## Overview

**JRoadSign** is a Java library tailored for representing and processing road signs as Java objects. It enables the
transformation of road sign data from various formats into structured Java objects, offering a flexible and powerful
interface for managing road sign information in diverse Java applications.

## Features

- **Flexible Data Handling**: Capable of processing road sign data from various sources, including but not limited to GeoJSON, depending on the specific requirements of each city module.
- **Comprehensive Data Access**: Provides extensive functions for accessing diverse properties of road signs, such as schedules, restrictions, and geographical coordinates.
- **Modular City-Specific Design**: Organized into city-specific sub-modules, each designed to cater to the unique road sign configurations of that city.

## Supported Cities

Currently, **JRoadSign** includes modules for the following city:

- [Montreal](docs/canada/quebec/montreal) - Data sourced from GeoJSON or CSV files
  from [Montreal Open Data](https://donnees.montreal.ca/dataset/).

Future expansions are planned to include additional cities with varying data sources and structures.

## Getting Started

### Prerequisites

- Java JDK (Version 17 or later recommended). Download from [Oracle Java SE Downloads](https://www.oracle.com/java/technologies/javase-downloads.html).

### Installation and Usage

1. **Module Integration**: Add the `JRoadSign` module to your Java project. For specific city data, import the
   respective city sub-module (e.g., `canada/quebec/montreal`).
2. **Data Interaction**: Use the provided functions within each sub-module to load and interact with road sign data, tailored to the data format and structure specific to that city.

#### Example

```java
import org.jroadsign.canada.quebec.montreal.RoadParkingSign;

// Load Montreal parking road sign data
RoadParkingSign roadParkingSign = new RoadParkingSign("path_to_your_file.geojson");
```

## Contributing
[//]: # (TODO : add contributing guidelines files in docs)
Contributions to JRoadSign are highly appreciated.
You can contribute in various ways, including reporting bugs, suggesting improvements, or adding new city modules, or
documentation.
Feel free to open issues or submit pull requests on our GitHub repository.

[//]: # (## Built With)

[//]: # (- [Maven]&#40;https://maven.apache.org/&#41; - Dependency management and build automation.)

## Authors

- [muhamm-ad](https://github.com/muhamm-ad) - Project Founder and Main Contributor.
