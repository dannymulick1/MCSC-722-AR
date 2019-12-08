package com.example.androiddemoar

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Camera
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.QuaternionEvaluator
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.math.Vector3Evaluator
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var fragment: ArFragment
    lateinit var fox: TransformableNode
    lateinit var pin: TransformableNode
    var nodes = arrayOfNulls<TransformableNode>(2)
    lateinit var camera: Camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment
        camera = fragment.arSceneView.scene.camera

        fab.setOnClickListener {
            if (::fox.isInitialized){
                fragment.arSceneView.scene.removeChild(fox)
                fox.renderable = null
            }
            addObject(Uri.parse("Mesh_Fox.sfb"))
        }
        moveFab.setOnClickListener{
            if (::pin.isInitialized){
                fragment.arSceneView.scene.removeChild(pin)
                pin.renderable = null
            }
            addObject(Uri.parse("CHAHIN_BOWLING_PIN.sfb"))
        }
        actFab.setOnClickListener{
            val objectAnimator = ObjectAnimator()
            objectAnimator.setAutoCancel(true)
            objectAnimator.target = fox
            // I use the direction method in order to flatten out the
            //  rotation angle, makes it so it only rotates on the y axis
            val direction = Vector3.subtract(fox.worldPosition,
                pin.worldPosition)
            var rotation = Quaternion.lookRotation(direction, Vector3.up())
            objectAnimator.setObjectValues(rotation)
            objectAnimator.setPropertyName("worldRotation")
            val v3Eval = QuaternionEvaluator()
            objectAnimator.setEvaluator(v3Eval)
            val inter = LinearInterpolator()
            objectAnimator.interpolator = inter
            objectAnimator.duration = 2000
            objectAnimator.start()
            objectAnimator.doOnEnd {
                val objectAnimator = ObjectAnimator()
                objectAnimator.setAutoCancel(true)
                objectAnimator.target = fox

                objectAnimator.setObjectValues(fox.worldPosition,
                    pin.worldPosition)
                objectAnimator.setPropertyName("worldPosition")
                val v3Eval = Vector3Evaluator()
                objectAnimator.setEvaluator(v3Eval)
                val inter = LinearInterpolator()
                objectAnimator.interpolator = inter
                objectAnimator.duration = 2000
                objectAnimator.start()

                objectAnimator.doOnEnd {
                    fragment.arSceneView.scene.removeChild(pin)
                    pin.renderable = null
                    val objectAnimator = ObjectAnimator()
                    objectAnimator.setAutoCancel(true)
                    objectAnimator.target = fox
                    val direction = Vector3.subtract(fox.worldPosition,
                        camera.worldPosition)
                    // Have to flatten out the y to stop the fox from tilting
                    // And neet to cast to float for vec3's
                    direction.y = 0.toFloat()
                    var rotation = Quaternion.lookRotation(direction, Vector3.up())
                    objectAnimator.setObjectValues(rotation)
                    objectAnimator.setPropertyName("worldRotation")
                    val v3Eval = QuaternionEvaluator()
                    objectAnimator.setEvaluator(v3Eval)
                    val inter = LinearInterpolator()
                    objectAnimator.interpolator = inter
                    objectAnimator.duration = 2000
                    objectAnimator.start()
                    objectAnimator.doOnEnd {

                        val objectAnimator = ObjectAnimator()
                        objectAnimator.setAutoCancel(true)
                        objectAnimator.target = fox
                        objectAnimator.setObjectValues(
                            fox.worldPosition,
                            Vector3(camera.worldPosition.x, fox.worldPosition.y,
                            camera.worldPosition.z)
                        )
                        objectAnimator.setPropertyName("worldPosition")
                        val v3Eval = Vector3Evaluator()
                        objectAnimator.setEvaluator(v3Eval)
                        val inter = LinearInterpolator()
                        objectAnimator.interpolator = inter
                        objectAnimator.duration = 2000
                        objectAnimator.start()
                    }
                }
            }

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
