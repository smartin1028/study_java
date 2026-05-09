/**
 * 워크플로우 실행 엔진
 *
 * 노드 배열을 순차적으로 실행하며, LLM 노드는 백엔드 /llm API 를 통해 처리한다.
 * 추후 DAG 기반 실행으로 확장 가능.
 */

import { callLLM } from './llmClient';

interface NodeData {
  id: string;
  label: string;
  agentType: string;
  prompt?: string;
  model?: string;
}

interface ExecutionResult {
  nodeId: string;
  success: boolean;
  output?: any;
  error?: string;
}

export class WorkflowExecutor {
  async executeNode(node: NodeData): Promise<ExecutionResult> {
    try {
      if (node.agentType === 'llm') {
        return await this.executeLLMNode(node);
      }

      // LLM 이외의 노드 타입은 즉시 성공 반환
      return {
        nodeId: node.id,
        success: true,
        output: { message: `${node.label} 노드 실행 완료` },
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
    if (!node.prompt) {
      throw new Error('프롬프트가 설정되지 않았습니다.');
    }

    // 백엔드 /llm API 를 통해 LLM 추론 실행
    const data = await callLLM([{ role: 'user', content: node.prompt }], node.model);

    return {
      nodeId: node.id,
      success: true,
      output: data,
    };
  }

  async executeWorkflow(
    nodes: any[],
    _edges: any[],
  ): Promise<ExecutionResult[]> {
    const results: ExecutionResult[] = [];

    // 순차 실행 (추후 DAG 기반 실행으로 확장 가능)
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
