precision highp float;

uniform vec3                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
varying vec2                texCoord;

float third = 0.33; //used for color choice thresholds

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	float thresh = 0.33;
    float greenness = length(	texture2D(iChannel0, fragCoord/iResolution.xy)
                              - normalize(vec4(13, 163, 38, 255))
                            );
    
    if (greenness < thresh)
    {
        fragColor = vec4(0.5, 0.5, 0.5, 0.5);
    }
    else if (greenness > third && greenness < 2.0*third) 
    {
        fragColor = vec4(0.0, 0.0, 0.0, 0.0);
    }
    else  
    {
    	fragColor = vec4(1.0, 1.0, 1.0, 1.0);   
    }
}

void main() {
	mainImage(gl_FragColor,  texCoord * iResolution.xy);
}