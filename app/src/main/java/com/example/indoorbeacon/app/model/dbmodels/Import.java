package com.example.indoorbeacon.app.model.dbmodels;

import com.example.indoorbeacon.app.model.RadioMap;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by TomTheBomb on 28.08.2015.
 */
public interface Import {

    public RadioMap load(InputStream is) throws IOException;

}
