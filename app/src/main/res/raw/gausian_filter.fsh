//#define GLARE_BLOOM //Gives a glare like bloom
precision mediump float;

uniform vec3                iResolution;
uniform sampler2D           iChannel0;
varying vec2                texCoord;


//#define GLARE_BLOOM //Gives a glare like bloom

vec3 getTexure(vec2 coord){
	return texture2D(iChannel0,coord.st).rgb;
}

vec3 getBloom(vec2 coord){

    const int samples = 6;
    vec3 color;
    float colorTresh;
    int weight;

    float size = 12.0; //size in pixels

    for (int i = 0; i < samples;i++){

        vec2 coord0 = coord + (vec2(i,i) * size / float(samples) / iResolution.xy);
        vec2 coord1 = coord + (vec2(-i,-i) * size / float(samples) / iResolution.xy);
        vec2 coord2 = coord + (vec2(-i,i) * size / float(samples) / iResolution.xy);
        vec2 coord3 = coord + (vec2(i,-i) * size / float(samples) / iResolution.xy);

        color += getTexure(coord0);
        color += getTexure(coord1);
        color += getTexure(coord2);
        color += getTexure(coord3);

        weight++;
    }

    color /= float(weight);
    color = pow(color,vec3(1.0));

    return (color / 4.0);
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy / iResolution.xy;

    #ifdef GLARE_BLOOM
		vec3 getColor = mix(getTexure(uv),getBloom(uv) * 2.0,0.5);
    #else
    	vec3 getColor = getBloom(uv);
    #endif

	fragColor = vec4(getColor,1.0);
}
void main() {
    mainImage(gl_FragColor, texCoord* iResolution.xy); // * iResolution.xy
}