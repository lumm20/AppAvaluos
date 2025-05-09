package mx.itson.moviles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView

class EmpresasFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_empresas, container, false)

        val empresa1 = view.findViewById<CardView>(R.id.empresa1)
        val empresa2 = view.findViewById<CardView>(R.id.empresa2)
        val empresa3 = view.findViewById<CardView>(R.id.empresa3)

        var empresaFragment = DetalleEmpresaFragment()

        empresa1.setOnClickListener {
            val src : Int = R.drawable.logo_gorozpe
            empresaFragment = DetalleEmpresaFragment.newInstance("Gorozpe & Robles",
                "Somos un despacho dedicado a la prestación de servicios legales y de avalúos. Brindamos asesoría y apoyo técnico y humano para encontrar las respuestas a los problemas y necesidades de nuestros clientes.",
                "Doctor Aguilar No. 138, entre Sahuaripa y Madrid; Col. Prados del Centenario. C.P. 83260; Hermosillo, Sonora, México.",
                src)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, empresaFragment)
                .commit()
        }
        empresa2.setOnClickListener {
            val src : Int = R.drawable.logo_noriega
            empresaFragment = DetalleEmpresaFragment.newInstance("Grupo Noriega Profesional",
                "Especialistas en avalúos de inmuebles, industriales, maquinaria y agropecuarios.",
                "Alfonso Ortiz Tirado No. 106; Col. Centro, casi esquina con Everardo Monroy; Hermosillo, Sonora, México",
                src)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, empresaFragment)
                .commit()
        }
        empresa3.setOnClickListener {
            val src : Int = R.drawable.camarillo_logo3
            empresaFragment = DetalleEmpresaFragment.newInstance("Camarillo",
                "En el año de 1970, Camarillo: Valuadores Profesionales comenzó a desempeñarse en el ámbito de los servicios de valuación, especializándose en construcciones, terrenos, maquinaria y equipo industrial y avalúos agropecuarios. Durante 47 años, los integrantes de Camarillo se han esforzado por generarle a las empresas o personas, documentación en donde quede plasmado la estimación precisa de sus inmuebles y además, se ofrece una orientación sobre los precios de la probable venta de los inmuebles.",
                "Francisco Márquez 23 esq. 14 de Abril; Col. La Huerta.; Hermosillo, Sonora, México",
                src)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, empresaFragment)
                .commit()
        }

        return view
    }

}