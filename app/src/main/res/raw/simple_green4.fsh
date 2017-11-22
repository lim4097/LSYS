precision highp float;

uniform vec3                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
uniform sampler2D           iChannel1;
varying vec2                texCoord;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy / iResolution.xy;
	vec4 sampled = texture2D(iChannel0,uv);
	vec4 fallback = 0.2 + texture2D(iChannel0,vec2(0.5, 1.0-uv.y)) * 0.3;

	// greenscreen remover
	//float dif = (sampled.r + sampled.b + 0.04) - sampled.g;
	//dif = clamp(dif+0.05, 0.0, 0.33) * 3.0;
	//sampled = mix(fallback, sampled, dif);

	//equalize
	//float sum = sampled.r + sampled.g + sampled.b;
	//sampled *= sum;

	//colorize
	vec4 color = vec4(uv*(0.5+0.5*sin(iGlobalTime*2.0)),0.5+0.5*sin(iGlobalTime*3.0),1.0) * 0.3;

	fragColor = sampled + color;
}

void main() {
	mainImage(gl_FragColor,  texCoord * iResolution.xy);
}