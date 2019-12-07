package com.example.androiddemoar

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.graphics.Path
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var fragment: ArFragment
    lateinit var fox: TransformableNode
    lateinit var pin: TransformableNode
    var nodes = arrayOfNulls<TransformableNode>(2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment


        fab.setOnClickListener { view ->
            addObject(Uri.parse("Mesh_Fox.sfb"))
        }
        moveFab.setOnClickListener{ view ->
            addObject(Uri.parse("CHAHIN_BOWLING_PIN.sfb"))
//            fragment.arSceneView.scene.removeChild(pin)
        }
    }

    private fun addObject(parse: Uri) {
        val frame = fragment.arSceneView.arFrame
        val point = getScreenCenter()
        if (frame != null) {
            val hits = frame.hitTest(point.x.toFloat(), point.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    placeObject(fragment, hit.createAnchor(), parse)
                    break
                }
            }
        }
    }

    private fun placeObject(fragment: ArFragment, createAnchor: Anchor, model: Uri) {
        ModelRenderable.builder()
            .setSource(fragment.context, model)
            .build()
            .thenAccept {
                if (model == Uri.parse("Mesh_Fox.sfb")){
                    addFoxToScene(fragment, createAnchor, it)
                }
                else{
                    addPinToScene(fragment, createAnchor, it)
                }

            }
            .exceptionally {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message)
                    .setTitle("error!")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }
    }

    private fun addFoxToScene(fragment: ArFragment, createAnchor: Anchor, renderable: ModelRenderable) {
//        val anchorNode = AnchorNode(createAnchor)
//        val rotatingNode = RotatingNode()
//
//        val transformableNode = TransformableNode(fragment.transformationSystem)
//
//        rotatingNode.renderable = renderable
//
//        rotatingNode.addChild(transformableNode)
//        rotatingNode.setParent(anchorNode)
//
//        fragment.arSceneView.scene.addChild(anchorNode)
//        transformableNode.select()
        val anchorNode = AnchorNode(createAnchor)
        this.fox = TransformableNode(fragment.transformationSystem)
        fox.renderable = renderable
        fox.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        fox.select()
        nodes[0] = fox
    }

    private fun addPinToScene(fragment: ArFragment, createAnchor: Anchor, renderable: ModelRenderable) {
//        val anchorNode = AnchorNode(createAnchor)
//        val rotatingNode = RotatingNode()
//
//        val transformableNode = TransformableNode(fragment.transformationSystem)
//
//        rotatingNode.renderable = renderable
//
//        rotatingNode.addChild(transformableNode)
//        rotatingNode.setParent(anchorNode)
//
//        fragment.arSceneView.scene.addChild(anchorNode)
//        transformableNode.select()
        val anchorNode = AnchorNode(createAnchor)
        this.pin = TransformableNode(fragment.transformationSystem)
        pin.renderable = renderable
        pin.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        pin.select()
        nodes[1] = pin
    }


    private fun getScreenCenter(): android.graphics.Point {
        val vw = findViewById<View>(android.R.id.content)
        return Point(vw.width / 2, vw.height / 2)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
