package com.iub.hometask.data.mock

object MockReplies {

    // +30 respuestas simulando conversación sobre tareas del hogar
    private val baseReplies = listOf(
        "Perfecto, ahora mismo lo reviso.",
        "¡Genial! Lo dejo listo antes de cenar.",
        "Lo tengo apuntado, gracias por recordarlo.",
        "Ok, me encargo en cuanto llegue a casa.",
        "Buen punto, así quedará mucho mejor.",
        "Yo ya hice la mitad, ahora continuo con el resto.",
        "¿Te parece si lo hago después de la comida?",
        "De acuerdo, hoy me encargo yo.",
        "Hecho, ya puedes comprobarlo.",
        "¡Listo! Quedó más limpio de lo esperado.",
        "Me falta sólo barrer y termino.",
        "¿Quieres que suba una foto cuando acabe?",
        "Lo haré con el producto nuevo que compramos.",
        "Entendido, voy a organizarlo por categorías.",
        "Tranquilo, yo lo hago más tarde.",
        "Buen recordatorio, casi se me olvida.",
        "Ok, lo haré antes de que se haga de noche.",
        "Gracias por avisar, lo reviso en un momento.",
        "Voy a intentar dejarlo mejor que la última vez.",
        "Si veo algo raro te aviso.",
        "Lo dejo preparado para mañana por la mañana.",
        "Ya he empezado, voy por la mitad.",
        "Creo que tardaré unos 20 minutos.",
        "Cuando termine te mando un mensaje.",
        "Lo haré mientras escucho música, así se hace más ameno.",
        "Te avisaré si necesito ayuda con algo.",
        "Voy a revisar también los cajones, ya que estoy.",
        "Intentaré no hacer ruido si estás trabajando.",
        "Perfecto, gracias por la indicación.",
        "Estoy en ello, cualquier cosa te escribo.",
        "¡Listo! Quedó todo ordenado y limpio.",
        "También tiré algunas cosas que ya no servían.",
        "Me faltó un poco de tiempo, pero mañana lo remato."
    )

    fun randomTaskReply(): String = baseReplies.random()

    fun randomChatReply(): String = baseReplies.random()
}
