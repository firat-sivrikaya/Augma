package world.augma.ui.AR;

public class AugmaPOI {
    private int mId;
    private String mName;
    private String mDescription;
    private double mLatitude;
    private double mLongitude;

    public AugmaPOI(String newName, String newDescription,
                        double newLatitude, double newLongitude) {
        this.mName = newName;
        this.mDescription = newDescription;
        this.mLatitude = newLatitude;
        this.mLongitude = newLongitude;
    }

    public int getPoiId() {
        return mId;
    }
    public void setPoiId(int poiId) {
        this.mId = poiId;
    }
    public String getPoiName() {
        return mName;
    }
    public void setPoiName(String poiName) {
        this.mName = poiName;
    }
    public String getPoiDescription() {
        return mDescription;
    }
    public void setPoiDescription(String poiDescription) {
        this.mDescription = poiDescription;
    }
    public double getPoiLatitude() {
        return mLatitude;
    }
    public void setPoiLatitude(double poiLatitude) {
        this.mLatitude = poiLatitude;
    }
    public double getPoiLongitude() {
        return mLongitude;
    }
    public void setPoiLongitude(double poiLongitude) {
        this.mLongitude = poiLongitude;
    }
}