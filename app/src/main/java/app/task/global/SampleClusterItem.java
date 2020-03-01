package app.task.global;
import androidx.annotation.Nullable;
import com.google.android.gms.maps.model.LatLng;
import net.sharewire.googlemapsclustering.ClusterItem;

public class SampleClusterItem implements ClusterItem {

    private final LatLng location;
    private String  Country;

   public  SampleClusterItem(String Country , LatLng location) {
        this.location = location;
        this.Country = Country;
    }



    @Override
    public double getLatitude() {
        return location.latitude;
    }

    public String getCountry() {
        return Country;
    }

    @Override
    public double getLongitude() {
        return location.longitude;
    }

    @Nullable
    @Override
    public String getTitle() {
        return null;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return null;
    }

}
