package com.greyogproducts.greyog.lechmap

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_stone.*
import org.xmlpull.v1.XmlPullParser

class StoneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stone)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val intnt = intent
        val s = intnt.extras["stoneNumber"] as String
//        Toast.makeText(this, "tap on marker $s listener", Toast.LENGTH_SHORT).show()
        supportActionBar?.title = s
        val parser = resources.getXml(R.xml.stones)
        var foundStone = false
        var llRoutes: LinearLayout? = null
        while (parser.next() != XmlPullParser.END_DOCUMENT && !foundStone) {
            if (parser.eventType == XmlPullParser.START_TAG &&
                    parser.name == "stone")
                if (parser.getAttributeValue(0) == s) {
                    foundStone = true
                    parser.next()
                    while (parser.name != "stone") {
//                        println(parser.name)
//                        for (i in 0 until parser.attributeCount) {
//                            println(parser.getAttributeName(i) + ": " + parser.getAttributeValue(i))
//                        }
                        if (parser.eventType == XmlPullParser.START_TAG) {
                            when (parser.name) {
                                "image" -> {
                                    val imgView = com.jsibbold.zoomage.ZoomageView(this)
                                    imgView.layoutParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT)
                                    val filename = parser.getAttributeValue(0).split(".")[0]
                                    val resId = resources.getIdentifier(filename, "drawable", this.packageName)
                                    println("$filename : $resId")
                                    imgView.setImageResource(resId)
                                    imgView.adjustViewBounds = true
                                    ll_stone.addView(imgView)

                                }
                                "route" -> {
                                    //TODO add textview
                                    llRoutes = LinearLayout(this)
                                    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT)
                                    layoutParams.topMargin = dpToPixels(8)
                                    llRoutes.layoutParams = layoutParams
                                    llRoutes.orientation = LinearLayout.HORIZONTAL
                                    ll_stone.addView(llRoutes)

                                    val imgMarker = ImageView(this)

                                    val colr = parser.getAttributeValue(0)
                                    println("color: $colr")
                                    imgMarker.layoutParams = ViewGroup.LayoutParams(dpToPixels(16), dpToPixels(16))
//                                    imgMarker.layoutParams = ViewGroup.LayoutParams(50, 50)
                                    imgMarker.setBackgroundColor(Color.parseColor("#$colr"))
                                    llRoutes.addView(imgMarker)
                                    val tv = TextView(this)
                                    val tvLayParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT)
                                    tvLayParam.marginStart = dpToPixels(8)
                                    tv.layoutParams = tvLayParam
                                    val txt = parser.getAttributeValue(1)
                                    println("text: $txt")
                                    tv.text = txt
                                    llRoutes.addView(tv)

                                }
                                else -> {
                                }
                            }
                        }
                        parser.next()
                    }
                }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun dpToPixels(dps: Int): Int {
        val scale = resources.displayMetrics.density
        return (dps * scale + 0.5f).toInt()
    }

}
