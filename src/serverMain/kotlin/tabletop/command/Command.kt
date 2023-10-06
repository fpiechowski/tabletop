package tabletop.command

/*context (Raise<Command.Exception>, Persistence)
actual fun <T : Command> T.execute() {
    *//*when (this) {
        is Command.Move<*> -> (((persistenceRoot
            .games[gameId] ?: raise(Command.EntityNotFoundException(gameId, Game::class)))
            .scenes[sceneId] ?: raise(Command.EntityNotFoundException(gameId, Scene::class)))
            .tokens[tokenId] ?: raise(Command.EntityNotFoundException(gameId, Token::class)))
            .move(Point(destination.x.toDouble(), destination.y.toDouble()))

        else -> {}
    }*//*
}*/
