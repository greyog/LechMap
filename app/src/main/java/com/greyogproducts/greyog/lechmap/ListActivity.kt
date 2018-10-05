package com.greyogproducts.greyog.lechmap

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.greyogproducts.greyog.lechmap.stonedata.StoneListContent


class ListActivity : AppCompatActivity() , ItemFragment.OnListFragmentInteractionListener{

    override fun onListFragmentInteraction(item: StoneListContent.StoneItem?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        Toast.makeText(this,"clicked on ${item?.name}", Toast.LENGTH_SHORT).show()
        val intent = Intent()
        intent.action = "com.greyogproducts.greyog.lechmap.SET_MAP_FOCUS"
        intent.putExtra("name", item?.name)
        sendBroadcast(intent)
        finish()
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
