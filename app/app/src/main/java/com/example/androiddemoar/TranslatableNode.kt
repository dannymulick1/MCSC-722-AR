package com.example.androiddemoar

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3

//https://www.raywenderlich.com/5485-arcore-sceneform-sdk-getting-started#toc-anchor-011
// Found above, this node can be used to allow animations to be done on a model

class TranslatableNode : Node() {

    fun addOffset(x: Float = 0F, y: Float = 0F, z: Float = 0F) {
        val posX = localPosition.x + x
        val posY = localPosition.y + y
        val posZ = localPosition.z + z

        localPosition = Vector3(posX, posY, posZ)
    }
}