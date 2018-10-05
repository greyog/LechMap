package com.greyogproducts.greyog.lechmap.stonedata

import android.content.Context
import android.graphics.Color
import com.greyogproducts.greyog.lechmap.R
import org.xmlpull.v1.XmlPullParser
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object StoneListContent {
    /**
     * An array of sample (dummy) mItems.
     */
    private var mItems: MutableList<StoneItem>? = null

    /**
     * A map of sample (dummy) mItems, by ID.
     */
    private val itemMap: MutableMap<String, StoneItem> = HashMap()

//    private val COUNT = 25

    fun getItems(context: Context): MutableList<StoneItem> {
        //        fill stones names, coordinates
        if (mItems != null) {
            return mItems as MutableList<StoneItem>
        }
        mItems = ArrayList()
        var curName = ""
        val parser1 = context.resources.getXml(R.xml.coords)
        while (parser1.eventType != XmlPullParser.END_DOCUMENT) {
//            println("name: ${parser.name}, text: ${parser.text}, eventType: ${parser.eventType}")
            if (parser1.name == "name")
                curName = parser1.nextText()
            if (parser1.name == "coordinates") {
                val coords = parser1.nextText()
                val stoneItem = StoneItem(curName, coords, emptyList<StoneImage>().toMutableList())
                addItem(stoneItem)
            }
            parser1.next()
        }
//        fill other stone data
        val parser = context.resources.getXml(R.xml.stones)
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG &&
                    parser.name == "stone") {
                val curStoneName = parser.getAttributeValue(0)
                val curStoneItem = itemMap[curStoneName]
                parser.next()
                while (parser.name != "stone") {
                    if (parser.eventType == XmlPullParser.START_TAG) {
                        var curStoneImage: StoneImage? = null
                        when (parser.name) {
                            "image" -> {
                                val curImgName = parser.getAttributeValue(0)
                                curStoneImage = StoneImage(curImgName, emptyList<Route>().toMutableList())
                                curStoneItem?.images?.add(curStoneImage)
                            }
                            "route" -> {
                                val colr = parser.getAttributeValue(0)
                                val txt = parser.getAttributeValue(1)
                                val route = Route(txt, Color.parseColor("#$colr"))
                                curStoneImage?.routes?.add(route)
                            }
                            else -> {
                            }
                        }
                    }
                    parser.next()
                }
            }

        }
        println(mItems)
        mItems?.sortBy {
            it.name.toIntOrNull()
        }

        return mItems as ArrayList<StoneItem>
    }

    private fun addItem(item: StoneItem) {
        mItems?.add(item)
        itemMap[item.name] = item
    }

//    private fun makeDetails(position: Int): String {
//        val builder = StringBuilder()
//        builder.append("Details about Item: ").append(position)
//        for (i in 0 until position) {
//            builder.append("\nMore details information here.")
//        }
//        return builder.toString()
//    }

    /**
     * A dummy item representing a piece of content.
     */
    data class StoneItem(val name: String, val coords: String, val images: MutableList<StoneImage>) {
        override fun toString(): String = "$name; $coords; $images"
    }

    data class StoneImage(val name: String, val routes: MutableList<Route>) {
        override fun toString(): String = "$name; $routes"
    }

    data class Route(val name: String, val color: Int) {
        override fun toString(): String = "$name; $color"
    }

}
