#version 330

//The job of the Fragment shader is to convert the output of the vertex shader into individual pixels and assign the final color that will be displayed on the screen.

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;
const float SPECULAR_POWER = 10;

in vec3 outPosition;
in vec3 outNormal;
in vec2 outTextCoord;

out vec4 fragColor;

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float reflectance;
};
struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};
struct AmbientLight
{
    float factor;
    vec3 color;
};
struct PointLight
{
    vec3 position;
    vec3 color;
    float intensity;
    Attenuation attenuation;
};
struct SpotLight
{
    PointLight pointLight;
    vec3 coneDirection;
    float cutoff;
};
struct DirectionalLight
{
    vec3 color;
    vec3 direction;
    float intensity;
};


uniform sampler2D textureSampler;
uniform Material material;
uniform AmbientLight ambientLight;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];
uniform DirectionalLight directionalLight;



vec4 calculateAmbient(AmbientLight ambientLight, vec4 ambient)
{
    return vec4(ambientLight.factor * ambientLight.color, 1) * ambient;
}

vec4 calculateLightColor(vec4 diffuse, vec4 specular, vec3 lightColor, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)
{
    vec4 diffuseColor = vec4(0, 0, 0, 1);
    vec4 specularColor = vec4(0, 0, 0, 1);

    //Calculate diffuse light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColor = diffuse * vec4(lightColor, 1.0) * light_intensity * diffuseFactor;

    //Calculate specular light
    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflected_light = normalize(reflect(from_light_dir, normal));
    float specularFactor = max(dot(camera_direction, reflected_light), 0.0);
    specularFactor = pow(specularFactor, SPECULAR_POWER);
    specularColor = specular * light_intensity * specularFactor * material.reflectance * vec4(lightColor, 1.0);

    return(diffuseColor + specularColor);
}

vec4 calculatePointLight(vec4 diffuse, vec4 specular, PointLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.position - position;
    vec3 to_light_dir = normalize(light_direction);
    vec4 light_color = calculateLightColor(diffuse, specular, light.color, light.intensity, position, to_light_dir, normal);

    //Add Attenuation
    float distance = length(light_direction);
    float attenuationInv = light.attenuation.constant + light.attenuation.linear * distance + light.attenuation.exponent * distance * distance;
    return light_color / attenuationInv;
}

vec4 calculateSpotLight(vec4 diffuse, vec4 specular, SpotLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.pointLight.position - position;
    vec3 to_light_dir = normalize(light_direction);
    vec3 from_light_dir = -to_light_dir;
    float spot_alfa = dot(from_light_dir, normalize(light.coneDirection));

    vec4 color = vec4(0, 0, 0, 0);

    if(spot_alfa > light.cutoff)
    {
        color = calculatePointLight(diffuse, specular, light.pointLight, position, normal);
        color *=(1.0 -(1.0 - spot_alfa) / (1.0 - light.cutoff));
    }
    return color;
}

vec4 calculateDirectionalLight(vec4 diffuse, vec4 specular, DirectionalLight light, vec3 position, vec3 normal)
{
    return calculateLightColor(diffuse, specular, light.color, light.intensity, position, normalize(light.direction), normal);
}

void main()
{
    vec4 text_color = texture(textureSampler, outTextCoord);
    vec4 ambient = calculateAmbient(ambientLight, text_color + material.ambient);
    vec4 diffuse = text_color + material.diffuse;
    vec4 specular = text_color + material.specular;

    vec4 diffuseSpecularComp = calculateDirectionalLight(diffuse, specular, directionalLight, outPosition, outNormal);

    for (int i = 0; i < MAX_POINT_LIGHTS; i++)
    {
        //Check if we've used this light at all.
        if (pointLights[i].intensity > 0)
        {
            diffuseSpecularComp += calculatePointLight(diffuse, specular, pointLights[i], outPosition, outNormal);
        }
    }
    for (int i = 0; i < MAX_SPOT_LIGHTS; i++)
    {
        //Check if we've used this light at all.
        if (spotLights[i].pointLight.intensity > 0)
        {
            diffuseSpecularComp += calculateSpotLight(diffuse, specular, spotLights[i], outPosition, outNormal);
        }
    }

    fragColor = ambient + diffuseSpecularComp;
}