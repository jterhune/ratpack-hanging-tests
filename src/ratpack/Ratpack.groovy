import static ratpack.groovy.Groovy.ratpack

ratpack {

    handlers {
        get('ok', { ctx ->
            ctx.render('OK')
        })
    }
}
