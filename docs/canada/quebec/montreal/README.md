# JRoadSign Montreal Module

This is the Montréal submodule of the **JRoadSign** project, dedicated to processing and representing Montréal's road
signs as Java objects. It focuses on handling data specifically tailored for Montréal, sourced
from [Montreal Open Data](https://donnees.montreal.ca/dataset/).

## Data Source

For the Montreal module, road sign data is obtained from either GeoJSON or CSV files and can be retrieved
from [Montreal Open Data](https://donnees.montreal.ca/dataset/).

## Supported Signs

- Montréal Parking Signs imported using `RoadParkingSign`.

### Installation and Usage

1. **Module Integration**: Incorporate the Montreal submodule into your Java project alongside the main JRoadSign
   library.
2. **Data Loading**: Utilize the specific functions provided in this module to load and process road sign data for
   Montreal.

#### Example

```java
import org.jroadsign.canada.quebec.montreal.RoadParkingSign;
import org.jroadsign.canada.quebec.montreal.RoadPost;

import java.util.TreeMap;

public class Main {
   public static void main(String[] args) {
      RoadParkingSign roadParkingSign = new RoadParkingSign("path_to/Signalisation-Stationnement.geojson");
      TreeMap<Long, RoadPost> roadPosts = roadParkingSign.getRoadPosts();
      // Further processing...
   }
}
```

For more examples, see [MontrealExamples](MontrealExemples.java).

## Contributing

Contributions specifically to the Montreal module of JRoadSign are welcome. If you have suggestions, bug reports, or
enhancements, please feel free to open an issue or a pull request on the Montréal branch.

## Module Maintainers

- [muhamm-ad](https://github.com/muhamm-ad) - Module Lead and Primary Contributor.
