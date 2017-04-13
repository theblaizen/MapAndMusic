package owdienko.jaroslaw.testapplication2.Data;

/**
 * Created by Jaroslaw Owdienko on 4/12/2017. All rights reserved TestApplication2!
 */

public class GMapMarkersObject {

    private double lat, lng;
    private String countryCode, toponymName, name;

    public GMapMarkersObject(double lat, double lng, String countryCode, String toponymName, String name) {
        this.lat = lat;
        this.lng = lng;
        this.countryCode = countryCode;
        this.toponymName = toponymName;
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getToponymName() {
        return toponymName;
    }

    public void setToponymName(String countryName) {
        this.toponymName = countryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
