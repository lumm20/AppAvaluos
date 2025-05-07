package mx.itson.moviles

/**
 * Catálogo centralizado de características para avalúos.
 * Esta clase proporciona acceso estático a todas las características disponibles.
 */
object CatalogoCaracteristicas {
    
    /**
     * Obtiene las características del entorno según el lugar ID
     * @param lugarId el ID del lugar (10-14)
     * @return Lista de características para ese lugar
     */
    fun obtenerCaracteristicasEntorno(lugarId: Int): List<Caracteristica> {
        val caracteristicas = ArrayList<Caracteristica>()
        
        when (lugarId) {
            10 -> { // Instalaciones Hidráulicas
                caracteristicas.add(Caracteristica(1, "Tubería de cobre", false))
                caracteristicas.add(Caracteristica(2, "Tubería de PVC", false))
                caracteristicas.add(Caracteristica(3, "Tubería de CPVC", false))
                caracteristicas.add(Caracteristica(4, "Tubería galvanizada", false))
                caracteristicas.add(Caracteristica(5, "Tubería de polietileno", false))
                caracteristicas.add(Caracteristica(6, "Tubería de acero inoxidable", false))
                caracteristicas.add(Caracteristica(7, "Tubería de hierro fundido", false))
                caracteristicas.add(Caracteristica(8, "Sistema de bombeo", false))
                caracteristicas.add(Caracteristica(9, "Cisterna", false))
                caracteristicas.add(Caracteristica(10, "Tinaco", false))
                caracteristicas.add(Caracteristica(11, "Hidroneumático", false))
                caracteristicas.add(Caracteristica(12, "Válvulas de control", false))
                caracteristicas.add(Caracteristica(13, "Sistema de filtración", false))
            }
            11 -> { // Instalaciones Sanitarias
                caracteristicas.add(Caracteristica(14, "Tubería de PVC sanitario", false))
                caracteristicas.add(Caracteristica(15, "Tubería de concreto", false))
                caracteristicas.add(Caracteristica(16, "Tubería de polipropileno", false))
                caracteristicas.add(Caracteristica(17, "Drenaje conectado a red municipal", false))
                caracteristicas.add(Caracteristica(18, "Fosa séptica", false))
                caracteristicas.add(Caracteristica(19, "Biodigestor", false))
                caracteristicas.add(Caracteristica(20, "Registro sanitario", false))
                caracteristicas.add(Caracteristica(21, "Sistema de ventilación", false))
                caracteristicas.add(Caracteristica(22, "Tubería de desagüe", false))
                caracteristicas.add(Caracteristica(23, "Sistema de tratamiento de aguas residuales", false))
            }
            12 -> { // Instalaciones Eléctricas
                caracteristicas.add(Caracteristica(24, "Cableado de cobre", false))
                caracteristicas.add(Caracteristica(25, "Cableado de aluminio", false))
                caracteristicas.add(Caracteristica(26, "Tubería conduit", false))
                caracteristicas.add(Caracteristica(27, "Tubería galvanizada", false))
                caracteristicas.add(Caracteristica(28, "Interruptor general", false))
                caracteristicas.add(Caracteristica(29, "Tablero de distribución", false))
                caracteristicas.add(Caracteristica(30, "Apagadores y contactos", false))
                caracteristicas.add(Caracteristica(31, "Sistema de puesta a tierra", false))
                caracteristicas.add(Caracteristica(32, "Línea monofásica", false))
                caracteristicas.add(Caracteristica(33, "Línea bifásica", false))
                caracteristicas.add(Caracteristica(34, "Línea trifásica", false))
                caracteristicas.add(Caracteristica(35, "Sistema de iluminación LED", false))
                caracteristicas.add(Caracteristica(36, "Sistema de iluminación fluorescente", false))
            }
            13 -> { // Obras Complementarias
                caracteristicas.add(Caracteristica(37, "Cisterna de concreto", false))
                caracteristicas.add(Caracteristica(38, "Tinaco de asbesto", false))
                caracteristicas.add(Caracteristica(39, "Tinaco de polietileno", false))
                caracteristicas.add(Caracteristica(40, "Barda perimetral", false))
                caracteristicas.add(Caracteristica(41, "Pavimentación de concreto", false))
                caracteristicas.add(Caracteristica(42, "Pavimentación asfáltica", false))
                caracteristicas.add(Caracteristica(43, "Jardinería", false))
                caracteristicas.add(Caracteristica(44, "Sistema de riego", false))
                caracteristicas.add(Caracteristica(45, "Alumbrado exterior", false))
                caracteristicas.add(Caracteristica(46, "Portón eléctrico", false))
                caracteristicas.add(Caracteristica(47, "Cochera techada", false))
                caracteristicas.add(Caracteristica(48, "Piscina", false))
                caracteristicas.add(Caracteristica(49, "Terraza techada", false))
            }
            14 -> { // Elementos Accesorios
                caracteristicas.add(Caracteristica(50, "Espejos", false))
                caracteristicas.add(Caracteristica(51, "Muebles de baño", false))
                caracteristicas.add(Caracteristica(52, "Barandal de acero", false))
                caracteristicas.add(Caracteristica(53, "Barandal de madera", false))
                caracteristicas.add(Caracteristica(54, "Pasamanos", false))
                caracteristicas.add(Caracteristica(55, "Rejas de herrería", false))
                caracteristicas.add(Caracteristica(56, "Proyectores de iluminación", false))
                caracteristicas.add(Caracteristica(57, "Lámparas decorativas", false))
                caracteristicas.add(Caracteristica(58, "Cancelería de aluminio", false))
                caracteristicas.add(Caracteristica(59, "Cancelería de madera", false))
                caracteristicas.add(Caracteristica(60, "Mosquiteros", false))
                caracteristicas.add(Caracteristica(61, "Persianas", false))
                caracteristicas.add(Caracteristica(62, "Cortinas", false))
                caracteristicas.add(Caracteristica(63, "Sistema de seguridad (cámaras)", false))
                caracteristicas.add(Caracteristica(64, "Sistema de alarma", false))
            }
        }
        
        return caracteristicas
    }
    
    /**
     * Obtiene las características del inmueble según el tipo
     * @param tipo el tipo de característica ("Pisos", "Muros", "Plafones")
     * @return Lista de características para ese tipo
     */
    fun obtenerCaracteristicasInmueble(tipo: String): List<Caracteristica> {
        val caracteristicas = ArrayList<Caracteristica>()
        
        when (tipo) {
            "Pisos" -> {
                caracteristicas.add(Caracteristica(65, "Loseta cerámica", false))
                caracteristicas.add(Caracteristica(66, "Loseta cerámica con zoclos", false))
                caracteristicas.add(Caracteristica(67, "Loseta cerámica rectificada con zoclos", false))
                caracteristicas.add(Caracteristica(68, "Loseta vinílica", false))
                caracteristicas.add(Caracteristica(69, "Mosaico cerámico", false))
                caracteristicas.add(Caracteristica(70, "Laminado imitación duela", false))
                caracteristicas.add(Caracteristica(71, "Duela de cedro", false))
                caracteristicas.add(Caracteristica(72, "Alfombra", false))
                caracteristicas.add(Caracteristica(73, "Loseta antiderrapante", false))
                caracteristicas.add(Caracteristica(74, "Loseta cerámica y antiderrapante en zona húmeda", false))
                caracteristicas.add(Caracteristica(75, "Concreto pulido y antiderrapante en zona húmeda", false))
                caracteristicas.add(Caracteristica(76, "Loseta vinílica y antiderrapante en zona húmeda", false))
                caracteristicas.add(Caracteristica(77, "Mosaico cerámico y antiderrapante en zona húmeda", false))
                caracteristicas.add(Caracteristica(78, "Concreto cerámico y firme de concreto en zona húmeda", false))
                caracteristicas.add(Caracteristica(79, "Firme de concreto pulido", false))
                caracteristicas.add(Caracteristica(80, "Firme de concreto común", false))
                caracteristicas.add(Caracteristica(81, "Firme de concreto escobillado", false))
                caracteristicas.add(Caracteristica(82, "Firme de concreto lavado", false))
                caracteristicas.add(Caracteristica(83, "Huellas de concreto", false))
                caracteristicas.add(Caracteristica(84, "Adoquín", false))
                caracteristicas.add(Caracteristica(85, "Tierra y andadores de concreto", false))
                caracteristicas.add(Caracteristica(86, "Tierra", false))
                caracteristicas.add(Caracteristica(87, "Cantera", false))
                caracteristicas.add(Caracteristica(88, "Pasto sintético", false))
                caracteristicas.add(Caracteristica(89, "Pasto", false))
                caracteristicas.add(Caracteristica(90, "Firme de concreto con tratamiento epóxico", false))
                caracteristicas.add(Caracteristica(91, "Firme de concreto sin reglegar", false))
                caracteristicas.add(Caracteristica(92, "Empedrado", false))
                caracteristicas.add(Caracteristica(93, "Se supone firme de concreto pulido", false))
                caracteristicas.add(Caracteristica(94, "Alta de otro", false))
            }
            "Muros" -> {
                caracteristicas.add(Caracteristica(95, "Aplanado mortero cemento-arena y pintura", false))
                caracteristicas.add(Caracteristica(96, "Aplanado fino cemento-arena y pintura", false))
                caracteristicas.add(Caracteristica(97, "Aplanado mortero cemento arena sin pintura", false))
                caracteristicas.add(Caracteristica(98, "Pasta texturizada", false))
                caracteristicas.add(Caracteristica(99, "Block aparente y pintura", false))
                caracteristicas.add(Caracteristica(100, "Azulejo", false))
                caracteristicas.add(Caracteristica(101, "Aplanado de mezcla, pintura y azulejo en área húmeda", false))
                caracteristicas.add(Caracteristica(102, "Aplanado fino de mezcla, pintura y azulejo en área húmeda", false))
                caracteristicas.add(Caracteristica(103, "Aplanado mortero cemento arena sin pintura en área húmeda", false))
                caracteristicas.add(Caracteristica(104, "Pasta texturizada y azulejo en área húmeda", false))
                caracteristicas.add(Caracteristica(105, "Aplanado de yeso con pintura y azulejo en área húmeda", false))
                caracteristicas.add(Caracteristica(106, "Block aparente con pintura y azulejo en área húmeda", false))
                caracteristicas.add(Caracteristica(107, "Azulejo a 2.10m y aplanado de mezcla con pintura", false))
                caracteristicas.add(Caracteristica(108, "Azulejo a 0.90m y aplanado de mezcla con pintura", false))
                caracteristicas.add(Caracteristica(109, "Tirol lanzado", false))
                caracteristicas.add(Caracteristica(110, "Tirol planchado", false))
                caracteristicas.add(Caracteristica(111, "Ladrillo aparente", false))
                caracteristicas.add(Caracteristica(112, "Block aparente", false))
                caracteristicas.add(Caracteristica(113, "Barda con enjarre sin reglegar", false))
                caracteristicas.add(Caracteristica(114, "Mortero lanzado", false))
                caracteristicas.add(Caracteristica(115, "Acabado cerroteado", false))
                caracteristicas.add(Caracteristica(116, "Aplanado de mezcla y recubrimientos de granito", false))
                caracteristicas.add(Caracteristica(117, "Aplanado de mezcla y recubrimientos cerámicos", false))
                caracteristicas.add(Caracteristica(118, "Aplanado de mezcla y recubrimientos de piedra cultivada", false))
                caracteristicas.add(Caracteristica(119, "Aplanado de mezcla y azulejo en regular estado", false))
                caracteristicas.add(Caracteristica(120, "Aplanado de mezcla en regular estado", false))
                caracteristicas.add(Caracteristica(121, "Aplanado de yeso en regular estado", false))
                caracteristicas.add(Caracteristica(122, "Aplanado de mezcla vandalizado en mal estado", false))
                caracteristicas.add(Caracteristica(123, "Yeso vandalizado en mal estado", false))
                caracteristicas.add(Caracteristica(124, "Block aparente vandalizado en mal estado", false))
                caracteristicas.add(Caracteristica(125, "Azulejo vandalizado en mal estado", false))
                caracteristicas.add(Caracteristica(126, "Lámina acanalada", false))
                caracteristicas.add(Caracteristica(127, "Se supone aplanado de mezcla con pintura", false))
            }
            "Plafones" -> {
                caracteristicas.add(Caracteristica(128, "Acabado tipo tirol", false))
                caracteristicas.add(Caracteristica(129, "Tirol planchado", false))
                caracteristicas.add(Caracteristica(130, "Tirol y yeso con pintura", false))
                caracteristicas.add(Caracteristica(131, "Yeso acabado pintura", false))
                caracteristicas.add(Caracteristica(132, "Aplanado fino de yeso y pintura", false))
                caracteristicas.add(Caracteristica(133, "Yeso acabado esmalte", false))
                caracteristicas.add(Caracteristica(134, "Aplanado cemento-arena y pintura", false))
                caracteristicas.add(Caracteristica(135, "Pasta texturizada", false))
                caracteristicas.add(Caracteristica(136, "Aplanado cemento-arena sin pintura", false))
                caracteristicas.add(Caracteristica(137, "Aplanado fino con molduras de yeso", false))
                caracteristicas.add(Caracteristica(138, "Adoquín y vigas de madera", false))
                caracteristicas.add(Caracteristica(139, "Lámina y perfiles de madera", false))
                caracteristicas.add(Caracteristica(140, "Lámina y perfiles metálicos", false))
                caracteristicas.add(Caracteristica(141, "Plafones de poliestireno expandido con cancelería", false))
                caracteristicas.add(Caracteristica(142, "Fibra de vidrio y perfiles metálicos", false))
                caracteristicas.add(Caracteristica(143, "Teja y perfiles metálicos", false))
                caracteristicas.add(Caracteristica(144, "Lona y perfiles metálicos", false))
                caracteristicas.add(Caracteristica(145, "Mallasombra y perfiles metálicos", false))
                caracteristicas.add(Caracteristica(146, "Insulpanes y perfiles metálicos", false))
                caracteristicas.add(Caracteristica(147, "Pergolado", false))
                caracteristicas.add(Caracteristica(148, "Losa aparente sin enjarre", false))
                caracteristicas.add(Caracteristica(149, "Aplanado de mezcla en regular estado", false))
                caracteristicas.add(Caracteristica(150, "Yeso terminado pintura en regular estado", false))
                caracteristicas.add(Caracteristica(151, "Losa en mal estado con acero expuesto", false))
                caracteristicas.add(Caracteristica(152, "Losa quemada en mal estado", false))
                caracteristicas.add(Caracteristica(153, "Se supone tirol", false))
            }
        }
        
        return caracteristicas
    }
    
    /**
     * Obtiene el nombre de una característica según su ID
     * @param idCaracteristica ID de la característica a buscar
     * @return Nombre de la característica o texto genérico si no se encuentra
     */
    fun obtenerNombreCaracteristica(idCaracteristica: Int): String {
        val mapaNombres = mutableMapOf<Int, String>()
        
        for (lugarId in 10..14) {
            obtenerCaracteristicasEntorno(lugarId).forEach { 
                mapaNombres[it.idCaracteristica] = it.nombre 
            }
        }
        
        listOf("Pisos", "Muros", "Plafones").forEach { tipo ->
            obtenerCaracteristicasInmueble(tipo).forEach { 
                mapaNombres[it.idCaracteristica] = it.nombre 
            }
        }
        
        return mapaNombres[idCaracteristica] ?: "Característica Desconocida"
    }
    
    /**
     * Obtiene el tipo de lugar según su ID
     * @param lugarId ID del lugar (10-14 para entorno, 1-9 para inmueble)
     * @return Nombre descriptivo del lugar
     */
    fun obtenerNombreTipoLugar(lugarId: Int): String {
        return when (lugarId) {
            10 -> "Instalaciones Hidráulicas"
            11 -> "Instalaciones Sanitarias"
            12 -> "Instalaciones Eléctricas"
            13 -> "Obras Complementarias"
            14 -> "Elementos Accesorios"
            1 -> "Sala"
            2 -> "Comedor"
            3 -> "Cocina"
            4 -> "Baño"
            5 -> "Recámara"
            6 -> "Estancia"
            7 -> "Patio Posterior"
            8 -> "Estacionamiento"
            9 -> "Terraza"
            else -> "Desconocido"
        }
    }
}