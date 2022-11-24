#
# Anserini: A Lucene toolkit for reproducible information retrieval research
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

import os
import time

file = 'src/main/python/regression-order-tuna.txt'
load_target = 10

with open(file) as f:
    lines = f.read().splitlines()

for r in lines:
    print(r)
    os.system(r + ' &')

    while True:
        time.sleep(5)
        load = os.getloadavg()[0]
        print(f'Current load: {load}')
        if load < load_target:
            break

