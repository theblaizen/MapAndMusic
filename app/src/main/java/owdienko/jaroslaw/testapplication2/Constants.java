package owdienko.jaroslaw.testapplication2;

/**
 * Created by Jaroslaw Owdienko on 4/11/2017. All rights reserved TestApplication2!
 */

public class Constants {
    //log filter
    public static final String MAIN_ACTIVITY_DEBUG_TAG = "debugger";

    //geonames api
    public static final String MAIN_ACTIVITY_JSON_API_URL = "http://api.geonames.org/searchJSON?" +
            "&country=UA&style=short&radius=100&localCountry=true&maxRows=100&cities=cities1000&";
    public static final String MAIN_ACTIVITY_JSON_API_KEY = "&username=theblaizen";
    //http://api.geonames.org/searchJSON?name_startsWith=k&country=UA&style=short&radius=100&localCountry=true&maxRows=30&cities=cities1000&username=theblaizen


    public static final int[] MUSIC_LIST = {
            R.raw.ramin_djawadi_westworld_ost,
            R.raw.kaleo_way_down_we_go,
            R.raw.the_lord_of_the_rings_the_shire,
            R.raw.bach_suite_cello_solo_in_g_major,
            R.raw.dustin_ohalloran_opus,
            R.raw.hans_zimmer_interstellar_main_theme,
            R.raw.sting_shape_of_my_heart,
            R.raw.tstm_was_it_a_dream
    };
}
