package com.abstractphil.pumpkin.cfg;

import java.util.Map;

import lombok.Data;

@Data
public class PumpkinConfig {
	private Map<String, PumpkinEffectData> effects;
}
