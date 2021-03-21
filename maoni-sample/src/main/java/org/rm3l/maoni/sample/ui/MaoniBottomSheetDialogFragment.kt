package org.rm3l.maoni.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.rm3l.maoni.Maoni
import org.rm3l.maoni.sample.R
import org.rm3l.maoni.sample.utils.MaoniUtils

class MaoniBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var maoni: Maoni? = null

    override fun getTheme(): Int = R.style.RoundedBottomSheetDialog

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(
            R.layout.bottom_sheet_dialog_fragment_maoni,
            container,
            false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<FloatingActionButton>(R.id.fab)
                ?.setOnClickListener {
                    maoni = MaoniUtils.buildMaoni(requireContext())
                    maoni?.start(dialog)
                }
    }

    override fun onDestroy() {
        super.onDestroy()
        maoni?.clear()
    }
}
