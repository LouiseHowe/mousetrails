package com.example.mousetrails;

import net.fabricmc.api.ClientModInitializer;

public class MouseTrailsModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MouseInputHandler.initialize();
    }
}