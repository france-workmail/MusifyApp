package france.apps.musify.menufragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast

import france.apps.musify.R

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v =  inflater.inflate(R.layout.fragment_home, container, false)
        return v
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         
         
         view.findViewById<ImageButton>(R.id.ibInfo).setOnClickListener {
             v ->
             Toast.makeText(activity,"Created by: France Gelasque. App Icon credits to: smalllikeart of www.flaticon.com",Toast.LENGTH_SHORT).show()
         }

     }


}
