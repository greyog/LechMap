package com.greyogproducts.greyog.lechmap

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.greyogproducts.greyog.lechmap.stonedata.StoneListContent


class ListActivity : AppCompatActivity() , ItemFragment.OnListFragmentInteractionListener{

    override fun onListFragmentInteraction(item: StoneListContent.StoneItem?, viewOnMap: Boolean) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if (viewOnMap) {
            val intent = Intent()
            intent.action = "com.greyogproducts.greyog.lechmap.SET_MAP_FOCUS"
            intent.putExtra("name", item?.name)
            sendBroadcast(intent)
            finish()
        }else {
            val intent = Intent(this, StoneActivity::class.java)
            intent.putExtra("stoneNumber", item?.name)
            this.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
