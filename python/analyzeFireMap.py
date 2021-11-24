from PIL import Image, ImageCms
from datetime import date
from datetime import datetime
import requests
import json
from camunda.external_task.external_task import ExternalTask, TaskResult
from camunda.external_task.external_task_worker import ExternalTaskWorker
import cv2
import base64
import io



# configuration for the Client
default_config = {
    "maxTasks": 1,
    "lockDuration": 10000,
    "asyncResponseTimeout": 5000,
    "retries": 3,
    "retryTimeout": 5000,
    "sleepSeconds": 30
}


def getRiskZone(zone_color):
    risk_zones = [
        {"id:":1, "title":"Χαμηλή", "color":[165, 255, 165]},
        {"id:":2, "title":"Μέση", "color":[161, 201, 237]},
        {"id:":3, "title":"Υψηλή", "color":[255, 253, 5]},
        {"id:":4, "title":"Πολύ υψηλή", "color":[254, 172, 2]},
        {"id:":5, "title":"Κατάσταση συναγερμού", "color":[255, 0, 0]},
        ]

    selected_risk_zone = risk_zones[0]
    distance = 99999999999999999;


    for rz in risk_zones:
        tmp_distance = (rz.get("color")[0] - zone_color[0])**2 + (rz.get("color")[1] - zone_color[1])**2 + (rz.get("color")[2] - zone_color[2])**2
        if (tmp_distance < distance):
            distance = tmp_distance
            selected_risk_zone = rz
    return selected_risk_zone;


def analyzeMap(map):
    # Get zones
    with open('assets/firezones.json', 'r', encoding="utf-8") as firezonesJsonFile:
        zones = json.load(firezonesJsonFile)

        # Prepare image
    im = map.convert('RGB')
    px = im.load()
    print("[x] The map image is ready for analysis")

    # Get risk
    for z in zones:
        x = int(z.get("map_point").get("x"))
        y = int(z.get("map_point").get("y"))
        zone_color = px[x, y]        
        z["status"] = getRiskZone(zone_color)

       
       # data = {'report':{'status':'Map found', 'date':analysis_date.strftime("%y-%m-%d")}, 'zones':zones}
        #with open('output/report{}.json'.format( analysis_date.strftime("%y%m%d") ), 'w', encoding='utf-8') as output_file:
      #      json.dump(data, output_file, ensure_ascii=False, indent=4)

    return zones
    
    
def stringToRGB(base64_string):
    imgdata = base64.b64decode(str(base64_string))
    image = Image.open(io.BytesIO(imgdata))
    return image


def handle_task(task: ExternalTask) -> TaskResult:
    """
    This task handler you need to implement with your business logic.
    After completion of business logic call either task.complete() or task.failure() or task.bpmn_error() 
    to report status of task to Camunda
    """
    # add your business logic here
    # ...

    # mark task either complete/failure/bpmnError based on outcome of your business logic
    
    print("inside Handler")

    map=task.get_variable("map_image")

    
    data=analyzeMap(stringToRGB(map))
    
    
    fire_services=[]


    fire_services_3=[i['name'] for i in data if i['status']['id:']==3] 
    fire_services_4=[i['name'] for i in data if i['status']['id:']==4]    
    fire_services_5=[i['name'] for i in data if i['status']['id:']==5]        
    
    

    return task.complete({"fire_services_3":fire_services_3,"fire_services_4":fire_services_4,"fire_services_5":fire_services_5})


if __name__ == '__main__':
   ExternalTaskWorker(worker_id="1", config=default_config).subscribe("process_map", handle_task)




