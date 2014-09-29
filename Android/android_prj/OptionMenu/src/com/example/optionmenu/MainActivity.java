package com.example.optionmenu;

import com.example.optionmenu.R.id;
import com.example.optionmenu.R.string;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case id.itemAdd:
			Toast.makeText(getApplicationContext(), string.itemAddSelect, Toast.LENGTH_SHORT).show();
			break;
		case id.itemEdit:
			Toast.makeText(getApplicationContext(), string.itemEditSelect, Toast.LENGTH_SHORT).show();
			break;
		case id.itemDel:
			Toast.makeText(getApplicationContext(), string.itemDelSelect, Toast.LENGTH_SHORT).show();
			break;
		case id.itemShare:
			Toast.makeText(getApplicationContext(), string.itemShareSelect, Toast.LENGTH_SHORT).show();
			break;
		case id.itemHelp:
			Toast.makeText(getApplicationContext(), string.itemHelpSelect, Toast.LENGTH_SHORT).show();
			break;
		case id.itemAbout:
			Toast.makeText(getApplicationContext(), string.itemAboutSelect, Toast.LENGTH_SHORT).show();
			break;		
		}		
		return false;
	}

    
}
