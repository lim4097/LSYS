precision highp float;

uniform vec3                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
varying vec2                texCoord;

mat4 brightnessMatrix( float brightness )
{
    return mat4( 1, 0, 0, 0,
                 0, 1, 0, 0,
                 0, 0, 1, 0,
                 brightness, brightness, brightness, 1 );
}

mat4 contrastMatrix( float contrast )
{
	float t = ( 1.0 - contrast ) / 2.0;
    
    return mat4( contrast, 0, 0, 0,
                 0, contrast, 0, 0,
                 0, 0, contrast, 0,
                 t, t, t, 1 );

}

mat4 saturationMatrix( float saturation )
{
    vec3 luminance = vec3( 0.3086, 0, 0 );
    
    float oneMinusSat = 1.0 - saturation;
    
    vec3 red = vec3( luminance.x * oneMinusSat );
    red+= vec3( saturation, 0, 0 );
    
    vec3 green = vec3( luminance.y * oneMinusSat );
    green += vec3( 0, saturation, 0 );
    
    vec3 blue = vec3( luminance.z * oneMinusSat );
    blue += vec3( 0, 0, saturation );
    
    return mat4( red,     0,
                 green,   0,
                 blue,    0,
                 0, 0, 0, 1 );
}

const float brightness = 0.2;
const float contrast = 1.0;
const float saturation = 0.0;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec4 color = texture2D( iChannel0, fragCoord/iResolution.xy );
    
	fragColor = brightnessMatrix( brightness ) *
        		contrastMatrix( contrast ) * 
        		saturationMatrix( saturation ) *
        		color;
	
}

void main() {
	mainImage(gl_FragColor,  texCoord * iResolution.xy);
}