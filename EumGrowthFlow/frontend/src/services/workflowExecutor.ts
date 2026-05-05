interface NodeData {
  id: string;
  label: string;
  agentType: string;
  prompt?: string;
  apiUrl?: string;
  apiMethod?: string;
  apiKeyName?: string;
  model?: string;
}

interface ExecutionResult {
  nodeId: string;
  success: boolean;
  output?: any;
  error?: string;
}

export class WorkflowExecutor {
  private getApiKey(keyName: string): string | null {
    return localStorage.getItem(`apikey_${keyName}`);
  }

  async executeNode(node: NodeData): Promise<ExecutionResult> {
    try {
      if (node.agentType === 'llm') {
        return await this.executeLLMNode(node);
      }

      // 다른 노드 타입 처리
      return {
        nodeId: node.id,
        success: true,
        output: { message: `${node.agentType} 노드 실행 완료` },
      };
    } catch (error: any) {
      return {
        nodeId: node.id,
        success: false,
        error: error.message,
      };
    }
  }

  private async executeLLMNode(node: NodeData): Promise<ExecutionResult> {
    if (!node.apiUrl) {
      throw new Error('API URL이 설정되지 않았습니다.');
    }

    if (!node.prompt) {
      throw new Error('프롬프트가 설정되지 않았습니다.');
    }

    let apiKey: string | null = null;
    if (node.apiKeyName) {
      apiKey = this.getApiKey(node.apiKeyName);
      if (!apiKey) {
        throw new Error(`API Key를 찾을 수 없습니다: ${node.apiKeyName}`);
      }
    }

    const method = node.apiMethod || 'POST';
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };

    if (apiKey) {
      headers['Authorization'] = `Bearer ${apiKey}`;
    }

    const requestBody = {
      model: node.model || 'llama2',
      prompt: node.prompt,
      stream: false,
      // OpenAI 형식 예시 (필요시 사용)
      messages: [
        {
          role: 'user',
          content: node.prompt,
        },
      ],
    };

    const response = await fetch(node.apiUrl, {
      method,
      headers,
      body: method !== 'GET' ? JSON.stringify(requestBody) : undefined,
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`API 호출 실패 (${response.status}): ${errorText}`);
    }

    const data = await response.json();

    return {
      nodeId: node.id,
      success: true,
      output: data,
    };
  }

  async executeWorkflow(nodes: any[], edges: any[]): Promise<ExecutionResult[]> {
    const results: ExecutionResult[] = [];

    // 간단한 순차 실행 (추후 DAG 기반 실행으로 확장 가능)
    for (const node of nodes) {
      const result = await this.executeNode({
        id: node.id,
        ...node.data,
      });
      results.push(result);
    }

    return results;
  }
}

export const workflowExecutor = new WorkflowExecutor();
