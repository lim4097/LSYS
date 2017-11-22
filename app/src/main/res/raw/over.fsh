precision highp float;

uniform vec3                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
varying vec2                texCoord;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy / iResolution.xy;
    
	fragColor = texture2D(iChannel0, uv);
}

void main() {
	mainImage(gl_FragColor,  texCoord * iResolution.xy);
}