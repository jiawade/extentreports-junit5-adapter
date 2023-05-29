
# extentreports-junit5-adapter #

extentreports-junit5-adapter is an adapter which is extent report generator 

## Installation

#### Maven
````xml
<dependency>
    <groupId>io.github.jiawade</groupId>
    <artifactId>extentreports-junit5-adapter</artifactId>
    <version>0.0.2</version>
</dependency>
````

#### Gradle
````gradle
compile 'io.github.jiawade:extentreports-junit5-adapter:0.0.1'
````

#### version mapping
* for extent-report4<===>0.0.1
* for extent-report5<===>0.0.2



## Usage Example
add following plugin:
````
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <configuration>
        <includes>
            <include>**/*.java</include>
        </includes>
    </configuration>
</plugin>
<plugin>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>2.22.2</version>
</plugin>
````
and then: mvn clean test

or
````java
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import io.github.adapter.Junit5ExtentReportListener;
import org.junit.jupiter.api.Test;

public class JunitTest extends Junit5ExtentReportListener {
    protected static ExtentTest test;
    protected static ExtentReports extent;


    @Test
    public void case01() {
        System.out.println("case one");
    }

    @Test
    public void case02() {
        System.out.println("case two");
    }

}
````

## Submitting Issues
For any issues or requests, please submit [here](https://github.com/jiawade/selenium-smart/issues)
